package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.dto.AlbaraMatchDTO;
import cat.ajterrassa.validaciofactures.dto.AlbaraSimpleDto;
import cat.ajterrassa.validaciofactures.dto.AutoRelateDTO;
import cat.ajterrassa.validaciofactures.dto.DetallDto;
import cat.ajterrassa.validaciofactures.dto.DetallMatchDTO;
import cat.ajterrassa.validaciofactures.dto.FacturaDto;
import cat.ajterrassa.validaciofactures.dto.ProveidorSimpleDto;
import cat.ajterrassa.validaciofactures.model.Albara;
import cat.ajterrassa.validaciofactures.model.AlbaraDetallMatch;
import cat.ajterrassa.validaciofactures.model.DocumentBase.EstatDocument;
import cat.ajterrassa.validaciofactures.model.Factura;
import cat.ajterrassa.validaciofactures.model.FacturaDetall;
import cat.ajterrassa.validaciofactures.model.Proveidor;
import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.repository.AlbaraRepository;
import cat.ajterrassa.validaciofactures.repository.FacturaDetallRepository;
import cat.ajterrassa.validaciofactures.repository.FacturaRepository;
import cat.ajterrassa.validaciofactures.repository.ProveidorRepository;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Objects;


@Service
@Transactional
public class FacturaService {

    private static final Logger logger = LoggerFactory.getLogger(FacturaService.class);

    private final FacturaRepository facturaRepository;
    private final FacturaDetallRepository facturaDetallRepository;
    private final UsuariService usuariService;
    private final ProveidorRepository proveidorRepository;
    private final AlbaraRepository albaraRepo;
    private final AlbaraDetallMatchService albaraDetallMatchService;



    public FacturaService(
            FacturaRepository facturaRepository,
            FacturaDetallRepository facturaDetallRepository,
            UsuariService usuariService,
            ProveidorRepository proveidorRepository,
            AlbaraRepository albaraRepo,
            AlbaraDetallMatchService albaraDetallMatchService

    ) {
        this.facturaRepository = facturaRepository;
        this.facturaDetallRepository = facturaDetallRepository;
        this.usuariService = usuariService;
        this.proveidorRepository = proveidorRepository;
        this.albaraRepo = albaraRepo;
        this.albaraDetallMatchService = albaraDetallMatchService;
    }

    public Factura save(FacturaDto dto, String emailUsuari) {
        Usuari creador = usuariService.findByEmail(emailUsuari);

        if (dto.getDetalls() == null || dto.getDetalls().isEmpty()) {
            throw new RuntimeException("Una factura ha de tenir almenys un detall");
        }

        BigDecimal sumaDetalls = dto.getDetalls().stream()
                .map(DetallDto::getImportTotalDetall)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal importTotalFactura = dto.getImportTotal(); // JA és BigDecimal
        if (sumaDetalls.compareTo(importTotalFactura) != 0) {
            throw new IllegalArgumentException("La suma dels detalls no coincideix amb l'import total de la factura.");
        }

        Factura factura = new Factura();
        factura.setCreador(creador);
Proveidor proveidor = proveidorRepository.findById(dto.getProveidor().getId())
        .orElseThrow(() -> new RuntimeException("Proveïdor no trobat"));

        factura.setProveidor(proveidor);
        factura.setData(dto.getData());
        factura.setImportTotal(importTotalFactura);
        factura.setEstat(EstatDocument.valueOf(dto.getEstat()));
        factura.setFitxerAdjunt(dto.getFitxerAdjunt());
        factura.setReferenciaDocument(dto.getReferenciaDocument());



        Factura savedFactura = facturaRepository.save(factura);

        List<FacturaDetall> detalls = dto.getDetalls().stream()
                .map(d -> {
                    FacturaDetall detall = new FacturaDetall();
                    detall.setImportTotalDetall(d.getImportTotalDetall()); // ja és BigDecimal
                    detall.setReferenciaDocumentDetall(d.getReferenciaDocumentDetall());
                    
                    detall.setFactura(savedFactura);
                    return detall;
                })
                .collect(Collectors.toList());

        facturaDetallRepository.saveAll(detalls);
        savedFactura.setDetalls(detalls); // opcional

        return savedFactura;
    }



@Transactional
    public Factura updateFactura(Long id, FacturaDto dto) {
        // 1) Recuperamos la factura existente
        Factura existent = facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no trobada"));

        // 2) Actualizamos los campos básicos de la factura
        existent.setData(dto.getData());
        existent.setImportTotal(dto.getImportTotal());
        existent.setFitxerAdjunt(dto.getFitxerAdjunt());
        existent.setReferenciaDocument(dto.getReferenciaDocument());

        // 3) Convertimos String a enum y guardamos el nuevo estado
        EstatDocument nouEstat = EstatDocument.valueOf(dto.getEstat());
        existent.setEstat(nouEstat);

        // 4) Si el estado cambia a VALIDAT, buscamos los albaranes y los marcamos también como VALIDAT
        if (nouEstat == EstatDocument.VALIDAT) {
            // Recuperamos todos los albaranes cuyo factura_id sea igual a id
            List<Albara> albarans = albaraRepo.findByFacturaId(existent.getId());

            for (Albara a : albarans) {
                a.setEstat(EstatDocument.VALIDAT);
                // Puesto que estamos en una transacción, si Albara está mapeado con cascade o la sesión sigue abierta,
                // bastaría con dejar que JPA detecte el cambio al hacer flush al finalizar el método.
                // No obstante, si no hay cascade, podemos forzar el save:
                albaraRepo.save(a);
            }
        }

        // 5) Guardamos y devolvemos la factura ya con el estado modificado
        return facturaRepository.save(existent);
    }




    public Factura updateFacturaAmbDetalls(Long id, FacturaDto dto) {
        Factura existent = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no trobada"));

        Map<Long, FacturaDetall> originals = existent.getDetalls().stream()
                .collect(Collectors.toMap(FacturaDetall::getId, Function.identity()));

        existent.getDetalls().removeIf(detall
                -> dto.getDetalls().stream().noneMatch(d -> d.getId() != null && d.getId().equals(detall.getId()))
        );

        for (DetallDto d : dto.getDetalls()) {
            logger.debug("🔁 Tractant línia: id={}, ref={}, import={}", d.getId(), d.getReferenciaDocumentDetall(), d.getImportTotalDetall());

            if (d.getId() == null) {
                FacturaDetall nou = new FacturaDetall();
                nou.setFactura(existent);
                nou.setImportTotalDetall(d.getImportTotalDetall()); // CORRECTE: ja és BigDecimal
                nou.setReferenciaDocumentDetall(d.getReferenciaDocumentDetall());
                existent.getDetalls().add(nou);
            } else {
                FacturaDetall existentDetall = originals.get(d.getId());
                if (existentDetall != null) {
                    existentDetall.setImportTotalDetall(d.getImportTotalDetall()); // CORRECTE
                    existentDetall.setReferenciaDocumentDetall(d.getReferenciaDocumentDetall());
                } else {
                    logger.warn("❗️Intent de modificar una línia de factura inexistent: id={}", d.getId());
                }
            }
        }

        return facturaRepository.save(existent);
    }

    public List<Factura> findAll() {
        return facturaRepository.findAll();
    }

    public Factura findById(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no trobada"));
    }

    public void delete(Long id) {
        facturaRepository.deleteById(id);
    }





/*************************************************************************/
//IMPLEMENTEM MACH, RELACIÓ DE FACTURES I ALBARANS
/*************************************************************************/



@Transactional(readOnly = true)
public List<Albara> getAlbaransCandidats(Long facturaId) {
    Factura f = findById(facturaId);
    Long provId = f.getProveidor().getId();
    return albaraRepo.findByFacturaIsNullAndProveidorId(provId);
}

@Transactional
public Factura afegirAlbaraManualment(Long facturaId, Long albaraId) {
    Factura f = findById(facturaId);
    Albara a = albaraRepo.findById(albaraId)
        .orElseThrow(() -> new RuntimeException("Albarà no trobat: " + albaraId));

    if (a.getFactura() != null) {
        throw new IllegalStateException("L'albarà ja està assignat a una factura");
    }

    if (!a.getProveidor().getId().equals(f.getProveidor().getId())) {
        throw new IllegalArgumentException("L'albarà no és del mateix proveïdor");
    }

    a.setFactura(f);
    albaraRepo.save(a);
    return f;
}

@Transactional
public void treureAlbaraDeFactura(Long facturaId, Long albaraId) {
    Factura f = findById(facturaId);
    Albara a = albaraRepo.findById(albaraId)
        .orElseThrow(() -> new RuntimeException("Albarà no trobat"));

    if (a.getFactura() == null || !a.getFactura().getId().equals(facturaId)) {
        throw new IllegalArgumentException("L'albarà no pertany a aquesta factura");
    }

    a.setFactura(null);
    albaraRepo.save(a);
}




@Transactional(readOnly = true)
public FacturaDto toDto(Factura f) {
    FacturaDto dto = new FacturaDto();
    dto.setId(f.getId());
    dto.setData(f.getData());
    dto.setTipus(f.getTipus());
    dto.setReferenciaDocument(f.getReferenciaDocument());
    dto.setImportTotal(f.getImportTotal());
    dto.setEstat(f.getEstat().name());
    dto.setFitxerAdjunt(f.getFitxerAdjunt());

    // Detalls de la factura
    dto.setDetalls(convertDetallsToDto(f.getDetalls()));

    // Proveïdor simple
    dto.setProveidor(new ProveidorSimpleDto(
        f.getProveidor().getId(),
        f.getProveidor().getNomComercial()
    ));

    // 🔗 Albarans amb referència coincident però sense factura assignada
    List<String> referenciesDetall = f.getDetalls().stream()
        .map(FacturaDetall::getReferenciaDocumentDetall)
        .filter(Objects::nonNull)
        .toList();

    List<Albara> candidats = albaraRepo.findByFacturaIsNullAndProveidorIdAndReferenciaDocumentIn(
        f.getProveidor().getId(),
        referenciesDetall
    );

    List<AlbaraSimpleDto> albaransRelacionats = candidats.stream()
        .map(a -> new AlbaraSimpleDto(a.getId(), a.getReferenciaDocument()))
        .toList();

    dto.setAlbaransRelacionats(albaransRelacionats);

    return dto;
}




@Transactional
public AutoRelateDTO autoRelate(Long facturaId) {
    Factura f = findById(facturaId);
    Long provId = f.getProveidor().getId();

    // Obtenim els detalls de la factura, els quals tenen referències a albarans
    List<FacturaDetall> detalls = f.getDetalls();

    // Agrupem els detalls per referència de document
    Map<String, List<FacturaDetall>> mapDetallsPerRef = detalls.stream()
        .filter(d -> d.getReferenciaDocumentDetall() != null)
        .collect(Collectors.groupingBy(FacturaDetall::getReferenciaDocumentDetall));

    // Busquem albarans que no tenen factura assignada i que coincideixen amb les referències dels detalls
    List<Albara> matchedA = albaraRepo
        .findByFacturaIsNullAndProveidorIdAndReferenciaDocumentIn(
            provId,
            new ArrayList<>(mapDetallsPerRef.keySet())
        );
    
    // Filtrar només aquells albarans que tenen import total coincident amb algun detall
    List<AlbaraMatchDTO> resultats = new ArrayList<>();

    // Agrupem els albarans per referència de document
    for (Albara a : matchedA) {
        List<FacturaDetall> relacionats = mapDetallsPerRef.getOrDefault(a.getReferenciaDocument(), List.of());
        // Si no hi ha detalls relacionats, continuem amb el següent albarà
        for (FacturaDetall d : relacionats) {
            boolean importCoincideix = d.getImportTotalDetall().compareTo(a.getImportTotal()) == 0;
            // Si l'import no coincideix, continuem amb el següent detall
            if (importCoincideix &&
                !albaraDetallMatchService.existsForDetall(d) &&
                !albaraDetallMatchService.existsForAlbara(a)) {
                // Si no hi ha relació prèvia, creem una nova relació
                a.setFactura(f); // vinculem
                albaraRepo.save(a);// guardem l'albarà amb la factura
                albaraDetallMatchService.crearRelacio(a, d); // ####!!!! creem la relació entre albarà i detall   
            }
        }

        // Si no hi ha detalls relacionats, continuem amb el següent albarà
        List<DetallMatchDTO> detallsRelacionats = relacionats.stream().map(d -> {
            DetallMatchDTO dto = new DetallMatchDTO();
            dto.setDetallId(d.getId());
            dto.setReferenciaDocumentDetall(d.getReferenciaDocumentDetall());
            dto.setImportTotalDetall(d.getImportTotalDetall());
            Albara albaraAssoc = getAlbaraAssociat(d);
            if (albaraAssoc != null) {
                dto.setAlbaraRelacionatId(albaraAssoc.getId());
            }
            return dto;
        }).collect(Collectors.toList());
        
        // Si no hi ha detalls relacionats, continuem amb el següent albarà
        AlbaraMatchDTO albaraMatch = new AlbaraMatchDTO();
        albaraMatch.setAlbaraId(a.getId());
        albaraMatch.setReferenciaAlbara(a.getReferenciaDocument());
        albaraMatch.setImportTotal(a.getImportTotal());
        albaraMatch.setLiniesFacturesRelacionades(detallsRelacionats);

        resultats.add(albaraMatch);// afegim a la llista de resultats 
    }
    // Retornem els resultats en un DTO
    AutoRelateDTO resposta = new AutoRelateDTO();
    resposta.setAlbaransAutoRelats(resultats);
    return resposta;
}




@Transactional(readOnly = true)
public List<DetallDto> convertDetallsToDto(List<FacturaDetall> detalls) {
    List<DetallDto> dtos = new ArrayList<>();
    for (FacturaDetall d : detalls) {
        DetallDto dto = new DetallDto();
        dto.setId(d.getId());
        dto.setReferenciaDocumentDetall(d.getReferenciaDocumentDetall());
        dto.setImportTotalDetall(d.getImportTotalDetall());
        dto.setFacturaId(d.getFactura().getId());

        // ✅ Associació amb albarà
        Albara albara = getAlbaraAssociat(d);
        if (albara != null) {
            dto.setAlbaraRelacionatId(albara.getId());
            dto.setReferenciaAlbaraRelacionat(albara.getReferenciaDocument()); // <- nou
            dto.setImportAlbaraRelacionat(albara.getImportTotal()); // <- nou
        }

        dtos.add(dto);
    }
    return dtos;
}


@Transactional
public void desvincularAlbara(Long facturaId, Long albaraId) {
    Factura f = findById(facturaId);
    Albara a = albaraRepo.findById(albaraId)
        .orElseThrow(() -> new RuntimeException("Albarà no trobat: " + albaraId));

    if (a.getFactura() == null || !a.getFactura().getId().equals(facturaId)) {
        throw new IllegalStateException("L'albarà no està vinculat a aquesta factura");
    }

    // Eliminar relació explícita si existeix
    if (albaraDetallMatchService.existsForAlbara(a)) {
        albaraDetallMatchService.eliminarRelacioPerAlbara(a);
    }

    // Desassignar la factura
    a.setFactura(null);
    albaraRepo.save(a);
}

@Transactional(readOnly = true)
public Albara getAlbaraAssociat(FacturaDetall detall) {
    return albaraDetallMatchService.getByDetall(detall)
            .map(AlbaraDetallMatch::getAlbara)
            .orElse(null);
}





}

