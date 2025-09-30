package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.dto.LoginRequest;
import cat.ajterrassa.validaciofactures.dto.LoginResponse;
import cat.ajterrassa.validaciofactures.security.JwtUtil;
import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import cat.ajterrassa.validaciofactures.dto.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuariRepository usuariRepository;

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Intentant login amb email: {}", request.getEmail());  // <--- Afegeix això
    try {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasenya())
        );

        Usuari usuari = usuariRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(auth.getName());

        logger.info("Token generat per a {}: {}", request.getEmail(), token);

        // Crea una resposta que inclou token, nom, id i contrasenyaTemporal
        LoginResponse response = new LoginResponse(
            token,
            usuari.getNom(),
            usuari.getId(),
            usuari.isContrasenyaTemporal(),
            usuari.getRol().name()
        );

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(401).body("Credencials incorrectes");
    }
}


// Endpoint per canviar la contrasenya     
@Autowired
private PasswordEncoder passwordEncoder;

@PostMapping("/change-password")
public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
    Usuari usuari = usuariRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new RuntimeException("Usuari no trobat"));

/*     if (!passwordEncoder.matches(request.getOldPassword(), usuari.getContrasenya())) {
        return ResponseEntity.badRequest().body("Contrasenya actual incorrecta");
    }*/
    // Si la contrasenya no és temporal, cal verificar la contrasenya antiga
    if (!usuari.isContrasenyaTemporal() && !passwordEncoder.matches(request.getOldPassword(), usuari.getContrasenya())) {
        return ResponseEntity.badRequest().body("Contrasenya actual incorrecta");
    }
    
    usuari.setContrasenya(passwordEncoder.encode(request.getNewPassword()));
    usuari.setContrasenyaTemporal(false);
    usuariRepository.save(usuari);

    return ResponseEntity.ok("Contrasenya canviada correctament");
}

}
