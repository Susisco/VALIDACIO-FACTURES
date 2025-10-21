package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.dto.AlbaraCreateDto;
import cat.ajterrassa.validaciofactures.dto.AlbaraDto;
import cat.ajterrassa.validaciofactures.dto.AlbaraMobileDto;
import cat.ajterrassa.validaciofactures.dto.UsuariSimpleDto;
import cat.ajterrassa.validaciofactures.model.*;
import cat.ajterrassa.validaciofactures.model.DocumentBase.EstatDocument;
import cat.ajterrassa.validaciofactures.service.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/albarans")
@CrossOrigin(origins = "${cors.allowed.origin}")// permet accés al frontend, configurat el port a application.properties 
public class AlbaraController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AlbaraController.class);

    private final AlbaraService albaraService;
    private final UsuariService usuariService;
    private final ProveidorService proveidorService;
    private final EdificiService edificiService;
    private final OTSService otsService;
    private final FacturaService facturaService;
    private final HistoricCanviService historicCanviService;

    public AlbaraController(
            AlbaraService albaraService,
            UsuariService usuariService,
            ProveidorService proveidorService,
            EdificiService edificiService,
            OTSService otsService,
            FacturaService facturaService,
            HistoricCanviService historicCanviService
    ) {
        this.albaraService = albaraService;
        this.usuariService = usuariService;
        this.proveidorService = proveidorService;
        this.edificiService = edificiService;
        this.otsService = otsService;
        this.facturaService = facturaService;
        this.historicCanviService = historicCanviService;
    }

    
     // --- Paràmetres de validació ---
    private static final long MAX_FILE_BYTES = 10L * 1024 * 1024; // 10 MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "application/pdf"
    );

    private ResponseEntity<String> validateUploadedFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("El fitxer és obligatori.");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            return ResponseEntity.badRequest().body("El fitxer és massa gran (màxim 10MB).");
        }
        // Nota: alguns navegadors poden enviar contentType buit; si passa,
        // ho validarem al servei pel nom/extensió com a “fallback”.
        String ct = file.getContentType();
        if (ct != null && !ALLOWED_CONTENT_TYPES.contains(ct)) {
            return ResponseEntity.badRequest().body("Tipus de fitxer no permès. Només JPG, PNG o PDF.");
        }
        return null; // OK
    }

    private ResponseEntity<?> saveAlbaraWithFileCommon(AlbaraCreateDto dto, MultipartFile file, Authentication authentication) {
        try {
            // 1) Validació del fitxer
            ResponseEntity<String> validation = validateUploadedFile(file);
            if (validation != null) return validation;

            // 2) Usuari autenticat
            String email = authentication.getName();
            Usuari creador = usuariService.findByEmail(email);
            if (creador == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuari no trobat.");
            }

            // 3) Guardar idempotentment: prioritzar submissionId i fer fallback a referència
            AlbaraService.SaveOrAttachResult result = albaraService.saveOrAttachByReferencia(dto, file, creador);

            // 4) Retorn 201 si creat, 200 si reutilitzat
            if (result.created) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result.albara);
            } else {
                return ResponseEntity.ok(result.albara);
            }

        } catch (Exception e) {
            logger.error("Error guardant albarà amb fitxer", e);
            // exposem el missatge per facilitar depuració (si vols, substitueix per missatge genèric)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ============================ FRONTEND Web
    @PostMapping(path = "/save-with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveAlbaraWithFileWeb(
            @RequestPart("data") AlbaraCreateDto dto,
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        return saveAlbaraWithFileCommon(dto, file, authentication);
    }

    // ============================ App (Android)
    @PostMapping(path = "/app/save-with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveAlbaraWithFileApp(
            @RequestPart("data") AlbaraCreateDto dto,
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        return saveAlbaraWithFileCommon(dto, file, authentication);
    }
    
    
    // ============================ CRUD per albarans

    @GetMapping
    public ResponseEntity<List<Albara>> all() {
        return ResponseEntity.ok(albaraService.findAll());
    }

    @GetMapping("/by-referencia/{referencia}")
    public ResponseEntity<?> getByReferencia(@PathVariable String referencia) {
        return albaraService.findByReferencia(referencia)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trobat"));
    }

    @GetMapping("/by-submission/{submissionId}")
    public ResponseEntity<?> getBySubmission(@PathVariable String submissionId) {
        return albaraService.findBySubmissionId(submissionId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trobat"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Albara> one(@PathVariable Long id) {
        return ResponseEntity.ok(albaraService.findById(id));
    }



    //###NOU### Endpoint per actualitzar un albarà existent
    @PutMapping("/{id}")
    public ResponseEntity<AlbaraDto> update(@PathVariable Long id, @RequestBody AlbaraDto dto) {
        Albara existing = albaraService.findById(id);

        applyDtoToEntity(dto, existing);
        existing.setId(id);

        // 🔄 Canviar l'estat de l'albarà a EN_CURS
        existing.setEstat(EstatDocument.EN_CURS);

        Albara saved = albaraService.save(existing);

        // 🔐 Obtenim l'usuari autenticat
        Usuari usuariAutenticat = usuariService.getUsuariAutenticat();

        // 📝 Registrem el canvi a l'històric
        historicCanviService.registrarCanvi(
                "Albara",
                saved.getId(),
                usuariAutenticat,
                "Modificació de l'albarà amb referència: " + saved.getReferenciaDocument()
        );

        AlbaraDto responseDto = new AlbaraDto();
        responseDto.setTipus(saved.getTipus());
        responseDto.setReferenciaDocument(saved.getReferenciaDocument());
        responseDto.setData(saved.getData());
        responseDto.setImportTotal(saved.getImportTotal());
        responseDto.setEstat(saved.getEstat());
        responseDto.setValidatPerId(saved.getValidatPer() != null ? saved.getValidatPer().getId() : null);
        responseDto.setProveidorId(saved.getProveidor().getId());
        responseDto.setEdificiId(saved.getEdifici() != null ? saved.getEdifici().getId() : null);
        responseDto.setOtsId(saved.getOts() != null ? saved.getOts().getId() : null);
        responseDto.setFacturaId(saved.getFactura() != null ? saved.getFactura().getId() : null);
        responseDto.setFitxerAdjunt(saved.getFitxerAdjunt());

        if (saved.getUsuariModificacio() != null) {
            responseDto.setUsuariModificacio(
                    new UsuariSimpleDto(
                            saved.getUsuariModificacio().getId(),
                            saved.getUsuariModificacio().getNom(),
                            saved.getUsuariModificacio().getEmail(),
                            saved.getUsuariModificacio().getRol().name()
                    )
            );
        }

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        albaraService.delete(id);
        return ResponseEntity.noContent().build();
    }

// Endpoint per obtenir albarans de l'usuari autenticat per utilitzar-ho a l'app
    @GetMapping("/me/mobile")
    public ResponseEntity<List<AlbaraMobileDto>> getAlbaransApp(Authentication authentication) {
        List<AlbaraMobileDto> albarans = albaraService.getAlbaransAppUsuari(authentication);
        return ResponseEntity.ok(albarans);
    }

    // (Fix de path) sense duplicar /albarans
    @PutMapping("/{id}/referencia")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('GESTOR')")
    public ResponseEntity<Albara> actualitzaReferencia(@PathVariable Long id, @RequestBody String novaReferencia) {
        return ResponseEntity.ok(albaraService.actualitzaReferencia(id, novaReferencia));
    }

    // Opcional: proxy cap al lector d'S3 del FitxerController
    @GetMapping("/{id}/file")
    public ResponseEntity<Void> getFileViaAlbarans(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create("/api/fitxers/albara/" + id))
                .build();
    }

    // -------- privat
    private void applyDtoToEntity(AlbaraDto dto, Albara a) {
        a.setTipus(dto.getTipus());
        a.setReferenciaDocument(dto.getReferenciaDocument());
        a.setData(dto.getData());
        a.setImportTotal(dto.getImportTotal());
        a.setEstat(dto.getEstat());

        // Creador pot ser null
        // Només incloure això si és una creació
        if (a.getId() == null) { // És una creació
            if (dto.getCreadorId() != null) {
                Usuari creador = usuariService.findById(dto.getCreadorId())
                        .orElseThrow(() -> new RuntimeException("Usuari no trobat: " + dto.getCreadorId()));
                a.setCreador(creador);
            } else {
                throw new RuntimeException("El camp creadorId és obligatori en la creació");
            }
        }

        // Validat per (opcional)
        if (dto.getValidatPerId() != null) {
            Usuari validatPer = usuariService.findById(dto.getValidatPerId())
                    .orElseThrow(() -> new RuntimeException("Usuari no trobat: " + dto.getValidatPerId()));
            a.setValidatPer(validatPer);
        } else {
            a.setValidatPer(null);
        }

        // Proveïdor (obligatori)
        Proveidor prov = proveidorService.findById(dto.getProveidorId())
                .orElseThrow(() -> new RuntimeException("Proveïdor no trobat: " + dto.getProveidorId()));
        a.setProveidor(prov);

        // Edifici pot ser null
        if (dto.getEdificiId() != null) {
            Edifici edif = edificiService.findById(dto.getEdificiId())
                    .orElseThrow(() -> new RuntimeException("Edifici no trobat: " + dto.getEdificiId()));
            a.setEdifici(edif);
        } else {
            a.setEdifici(null);
        }

        // OTS pot ser null
        if (dto.getOtsId() != null) {
            OTS ots = otsService.findById(dto.getOtsId())
                    .orElseThrow(() -> new RuntimeException("OTS no trobat: " + dto.getOtsId()));
            a.setOts(ots);
        } else {
            a.setOts(null);
        }

        // Factura pot ser null
        if (dto.getFacturaId() != null) {
            Factura f = null;
            try {
                f = facturaService.findById(dto.getFacturaId());
            } catch (RuntimeException ignored) {
            }
            a.setFactura(f);
        } else {
            a.setFactura(null);
        }

        a.setFitxerAdjunt(dto.getFitxerAdjunt());

        // ✅ Nou: Usuari que modifica (extret automàticament del context d'autenticació)
        Usuari usuariModificador = usuariService.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        if (usuariModificador == null) {
            throw new RuntimeException("Usuari autenticat no trobat");
        }
        a.setUsuariModificacio(usuariModificador);
    }
}
