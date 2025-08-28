package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.Albara;
import cat.ajterrassa.validaciofactures.repository.AlbaraRepository;
import cat.ajterrassa.validaciofactures.dto.AlbaraMobileDto;
import cat.ajterrassa.validaciofactures.dto.AlbaraCreateDto;
import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.model.Proveidor;
import cat.ajterrassa.validaciofactures.repository.HistoricCanviRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cat.ajterrassa.validaciofactures.repository.ProveidorRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;

import cat.ajterrassa.validaciofactures.model.DocumentBase.EstatDocument;

@Service
@Transactional
public class AlbaraService {

    @Autowired
    private AlbaraRepository albaraRepository;
    private final AlbaraRepository repo;
    @Autowired
    private ProveidorRepository proveidorRepository;
    @Autowired
    private HistoricCanviRepository historicCanviRepository;
    @Autowired
    private S3Service s3Service;

    public AlbaraService(AlbaraRepository repo) {
        this.repo = repo;
    }

    public List<Albara> findAll() {
        return repo.findAll();
    }

    public Albara findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Albarà no trobat: " + id));
    }

    public Albara save(Albara a) {
        return repo.save(a);
    }

    @Transactional
    public void delete(Long id) {
        Albara albara = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Albarà no trobat amb ID: " + id));

        // 1) historial
        historicCanviRepository.deleteByDocumentIdAndTipusDocument(id, "Albara");

        // 2) esborrar fitxer a S3 si existeix (accepta rutes antigues amb /uploads/)
        String fitxer = albara.getFitxerAdjunt();
        if (fitxer != null && !fitxer.isBlank()) {
            String s3Key = normalizeToS3Key(fitxer);
            try {
                s3Service.delete(s3Key);
            } catch (Exception e) {
                System.err.println("⚠️ No s'ha pogut eliminar de S3: " + s3Key + " -> " + e.getMessage());
            }
        }

        // 3) esborrar albarà
        repo.deleteById(id);
    }

    // ✅ Guardar el fitxer a S3 i emmagatzemar la KEY 
    public String guardarFitxer(Long id, MultipartFile file, String _unused, String subfolder)
            throws IOException {

        // 1. Carregar l'albarà existent
        Albara albara = albaraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Albarà no trobat amb ID: " + id));

        // 2. Extensió
        String extensio = getExtensioSegura(file.getOriginalFilename());

        // 3. Nom únic
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String nomFitxer = "albara_" + id + "_" + timestamp + extensio;

        // 4. KEY S3
        String s3Key = subfolder + "/" + nomFitxer;

        // 5. Pujar a S3
        s3Service.upload(
                s3Key,
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
        );

        // 6. ✅ Desa la KEY a BBDD 
        albara.setFitxerAdjunt(s3Key);
        albara.setActualitzat(LocalDateTime.now());
        albaraRepository.save(albara);

        return s3Key;
    }

    private String getExtensioSegura(String nomOriginal) {
        if (nomOriginal == null || !nomOriginal.contains(".")) {
            return ".jpg";
        }
        String ext = nomOriginal.substring(nomOriginal.lastIndexOf('.')).toLowerCase();
        return ext.matches("\\.(jpg|jpeg|png|pdf)") ? ext : ".jpg";
    }

    private String normalizeToS3Key(String stored) {
        if (stored == null) {
            return null;
        }
        String key = stored.replace("\\", "/");
        if (key.startsWith("/uploads/")) {
            key = key.substring("/uploads/".length());
        }
        while (key.startsWith("/")) {
            key = key.substring(1);
        }
        return key;
    }

    // --- APP ---
    public List<AlbaraMobileDto> getAlbaransAppUsuari(Authentication authentication) {
        String email = authentication.getName();
        List<Albara> albarans = albaraRepository.findByCreadorEmail(email);
        return albarans.stream()
                .map(a -> new AlbaraMobileDto(
                a.getId(),
                a.getReferenciaDocument(),
                a.getData() != null ? a.getData().toString() : null,
                a.getEstat(),
                a.getImportTotal() != null ? a.getImportTotal().toString() : null,
                a.getCreat() != null ? a.getCreat().toString() : null
        ))
                .collect(Collectors.toList());
    }

    // Guardar albarà + fitxer
    public Albara saveAlbaraWithFile(AlbaraCreateDto dto, MultipartFile file, Usuari creador) throws IOException {
        try {
            System.out.println("Iniciant el procés de guardar l'albarà");

            Albara albara = new Albara();
            //omplim els camps de l'albarà:
            albara.setTipus(dto.getTipus());
            albara.setReferenciaDocument(dto.getReferenciaDocument());
            albara.setData(LocalDate.parse(dto.getData()));
            albara.setImportTotal(BigDecimal.valueOf(dto.getImportTotal()));
            albara.setEstat(EstatDocument.valueOf(dto.getEstat()));
            albara.setCreador(creador);
            System.out.println("Buscant el proveïdor amb ID: " + dto.getProveidorId());
            Proveidor proveidor = proveidorRepository.findById(dto.getProveidorId())
                    .orElseThrow(() -> new RuntimeException("Proveïdor no trobat"));
            albara.setProveidor(proveidor);

            //guardem l'albarà a la base de dades, ens reveix perque quan seguidament guardem el fitxer comprovem que hi ha el registre creat
            System.out.println("Guardant l'albarà a la base de dades");
            albara = albaraRepository.save(albara);

            // Guardar fitxer a S3 i guardem el nom a s3Key per guardar nom a la base de dades
            System.out.println("Guardant el fitxer adjunt");
            String subfolder = "albarans";
            String s3Key = guardarFitxer(albara.getId(), file, null, subfolder);


            // guardem el nom de ruta fitxer al camp de l'albarà que estem creant
            albara.setFitxerAdjunt(s3Key);

            //tornem a actualitzar el registre creat amb la ruta del fitxer guardat
            System.out.println("Actualitzant l'albarà amb la ruta del fitxer");
            return albaraRepository.save(albara);

        } catch (Exception e) {
            System.err.println("Error al guardar l'albarà: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Albara actualitzaReferencia(Long id, String novaReferencia) {
        Albara a = albaraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Albarà no trobat"));
        a.setReferenciaDocument(novaReferencia);
        return albaraRepository.save(a);
    }
}
