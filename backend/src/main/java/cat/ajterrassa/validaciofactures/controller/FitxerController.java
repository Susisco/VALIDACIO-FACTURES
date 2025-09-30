package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.Albara;
import cat.ajterrassa.validaciofactures.service.AlbaraService;
import cat.ajterrassa.validaciofactures.service.PressupostService;
import cat.ajterrassa.validaciofactures.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/fitxers")
public class FitxerController {

    private final PressupostService pressupostService;
    private final AlbaraService albaraService;
    private final S3Service s3Service;

    public FitxerController(PressupostService pressupostService,
            AlbaraService albaraService,
            S3Service s3Service) {
        this.pressupostService = pressupostService;
        this.albaraService = albaraService;
        this.s3Service = s3Service;
    }

    @Value("${uploads.dir}")
    private String uploadsDir;

    @Value("${app.uploads.dir}")
    private String baseUploadDir;

    // 👉 FRONTEND: pujar fitxer de pressupost
    @PostMapping("/pressupost/{id}")
    public ResponseEntity<?> pujarFitxerPressupost(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String rutaRelativa = pressupostService.guardarFitxer(id, file, uploadsDir, "pressupostos");
            return ResponseEntity.ok().body("Fitxer pujat: " + rutaRelativa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al desar fitxer: " + e.getMessage());
        }
    }

    // 👉 FRONTEND: pujar fitxer d'albarà
    @PostMapping("/albara/{id}")
    public ResponseEntity<?> pujarFitxerAlbara(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String rutaRelativa = albaraService.guardarFitxer(id, file, uploadsDir, "albarans");
            return ResponseEntity.ok().body("Fitxer pujat: " + rutaRelativa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al desar fitxer: " + e.getMessage());
        }
    }

    // 👉 APP: pujar fitxer "lliure"
    @PostMapping("/upload")
    public ResponseEntity<String> pujarFitxerApp(@RequestParam("fitxer") MultipartFile fitxer) throws IOException {
        if (fitxer.isEmpty()) {
            return ResponseEntity.badRequest().body("Fitxer buit");
        }

        Path carpeta = Paths.get(baseUploadDir, "albarans");
        Files.createDirectories(carpeta);

        String nomFitxer = System.currentTimeMillis() + "_" + fitxer.getOriginalFilename();
        Path desti = carpeta.resolve(nomFitxer);

        Files.copy(fitxer.getInputStream(), desti, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("albarans/" + nomFitxer);
    }


    // ✅ NOVETAT: obtenir fitxer d'un albarà des de S3

    //variable per controlar el temps de validesa del enllaç a veure imatge, també hi ha config a propierties
    @Value("${s3.presign.ttl-seconds:120}")
    private int presignTtl;
    
    @GetMapping("/albara/{id}")
    public ResponseEntity<Void> obtenirFitxerAlbara(@PathVariable Long id) {
        Albara albara = albaraService.findById(id);
        String ruta = albara.getFitxerAdjunt();  // p.ex. "albarans/albara_123_..." o "/uploads/albarans/..."

        if (ruta == null || ruta.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        // Normalitza la clau d’S3, treu uploads dels entics noms a DB, això ho haig d'eliminar
        String s3Key = ruta.startsWith("/uploads/") ? ruta.substring("/uploads/".length()) : ruta;

        // 💡 URL signada temporal (p.ex. 120s)
        java.net.URL url = s3Service.presignGetUrl(s3Key, java.time.Duration.ofSeconds(presignTtl));

        // 302 Found -> redirecció a S3
        return ResponseEntity.status(302)
                .location(java.net.URI.create(url.toString()))
                .build();
    }

    // ✅ NOVETAT: endpoint proxy per servir fitxers directament (evita CORS)
    @GetMapping("/albara/{id}/download")
    public ResponseEntity<byte[]> descarregarFitxerAlbara(@PathVariable Long id) {
        try {
            Albara albara = albaraService.findById(id);
            String ruta = albara.getFitxerAdjunt();

            if (ruta == null || ruta.isBlank()) {
                return ResponseEntity.notFound().build();
            }

            // Normalitza la clau d'S3
            String s3Key = ruta.startsWith("/uploads/") ? ruta.substring("/uploads/".length()) : ruta;

            // Obtenim URL signada temporal
            URL url = s3Service.presignGetUrl(s3Key, java.time.Duration.ofSeconds(presignTtl));

            // Fem la petició HTTP a S3 i retornem el contingut com a proxy
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 segons
            connection.setReadTimeout(30000);    // 30 segons

            if (connection.getResponseCode() != 200) {
                return ResponseEntity.status(connection.getResponseCode()).build();
            }

            // Llegim tot el contingut
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] contingut = inputStream.readAllBytes();

                // Determinem el tipus MIME
                String mimeType = detectMimeType(ruta);
                String fileName = ruta.substring(ruta.lastIndexOf('/') + 1);

                // Configurem les capçaleres per la descàrrega
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(mimeType));
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
                headers.setContentLength(contingut.length);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(contingut);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    
    // 🔎 Detecció de tipus MIME per extensió
    private String detectMimeType(String filename) {
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (ext) {
            case "png" ->
                "image/png";
            case "jpg", "jpeg" ->
                "image/jpeg";
            case "pdf" ->
                "application/pdf";
            default ->
                "application/octet-stream";
        };
    }
}
