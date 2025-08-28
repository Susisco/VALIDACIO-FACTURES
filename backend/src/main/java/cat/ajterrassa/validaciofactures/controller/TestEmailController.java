package cat.ajterrassa.validaciofactures.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cat.ajterrassa.validaciofactures.service.EmailService;

@RestController
@RequestMapping("/api/test")
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/mail")
    public ResponseEntity<String> testMail() {
        String destinatari = "franceschidalgo@hotmail.com"; // 🔁 canvia-ho pel teu
        String contrasenyaTemporal = "ABC12345"; // prova

        emailService.sendPasswordEmail(destinatari, contrasenyaTemporal);

        return ResponseEntity.ok("📨 Correu de prova enviat a " + destinatari);
    }
}
