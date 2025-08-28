// src/main/java/cat/ajterrassa/validaciofactures/service/PressupostService.java
package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.Pressupost;
import cat.ajterrassa.validaciofactures.repository.PressupostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;



@Service
@Transactional
public class PressupostService {

    @Autowired
    private PressupostRepository pressupostRepository;

    private final PressupostRepository repo;

    public PressupostService(PressupostRepository repo) {
        this.repo = repo;
    }

    public List<Pressupost> findAll() {
        return repo.findAll();
    }

    public Pressupost findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pressupost no trobat: " + id));
    }

    public Pressupost save(Pressupost p) {
        return repo.save(p);
    }

   // public void delete(Long id) {
     //   repo.deleteById(id);
    //}

    @Value("${app.uploads.dir}")
    private String baseUploadDir;

    @Transactional
    public void delete(Long id) {
        Pressupost pressupost = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Pressupost no trobat amb ID: " + id));

        String fitxer = pressupost.getFitxerAdjunt();
        if (fitxer != null && !fitxer.isBlank()) {
            Path fitxerPath = Paths.get(baseUploadDir, "pressupostos")
                .resolve(Paths.get(fitxer).getFileName());

            try {
                Files.deleteIfExists(fitxerPath);
            } catch (IOException e) {
                System.err.println("⚠️ No s'ha pogut eliminar el fitxer: " + fitxerPath);
                // Opcional: log.warn(...) o throw si vols interrompre la transacció
            }
        }

        repo.deleteById(id);
    }

    public String guardarFitxer(Long id, MultipartFile file, String uploadsBaseDir, String subfolder)
            throws IOException {
        Pressupost pressupost = pressupostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pressupost no trobat amb ID: " + id));

        String extensio = getExtensioSegura(file.getOriginalFilename());
        // Generem un nom de fitxer amb la data i hora actual
        // i l'ID del pressupost
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String nomFitxer = "albara_" + id + "_" + timestamp + extensio;

        Path carpeta = Paths.get(uploadsBaseDir, subfolder);
        Files.createDirectories(carpeta);

        Path desti = carpeta.resolve(nomFitxer);

        // Mostrem per consola la ruta absoluta abans de guardar
        System.out.println("Ruta absoluta: " + desti.toAbsolutePath());

        file.transferTo(desti.toFile());

        String rutaRelativa = "/uploads/" + subfolder + "/" + nomFitxer;
        pressupost.setFitxerAdjunt(rutaRelativa);
        pressupost.setActualitzat(LocalDateTime.now());

        pressupostRepository.save(pressupost);
        return rutaRelativa;
    }

    private String getExtensioSegura(String nomOriginal) {
        if (nomOriginal == null || !nomOriginal.contains(".")) {
            return ".jpg";
        }
        String ext = nomOriginal.substring(nomOriginal.lastIndexOf('.')).toLowerCase();
        return ext.matches("\\.(jpg|jpeg|png|pdf)") ? ext : ".jpg";
    }

}
