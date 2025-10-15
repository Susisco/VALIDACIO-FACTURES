package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.dto.LoginRequest;
import cat.ajterrassa.validaciofactures.dto.LoginResponse;
import cat.ajterrassa.validaciofactures.security.JwtUtil;
import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;
import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;

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

    @Autowired
    private DeviceRegistrationRepository deviceRepository;

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request,
                              @RequestHeader(value = "X-Firebase-Installation-Id", required = false) String fid,
                              @RequestHeader(value = "X-App-Version", required = false) String appVersion) {
        logger.info("Intentant login amb email: {}", request.getEmail());  // <--- Afegeix això
    try {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasenya())
        );

        Usuari usuari = usuariRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(auth.getName());

        logger.info("Token generat per a {}: {}", request.getEmail(), token);

        // ⭐ NOVA FUNCIONALITAT: Associar dispositiu amb usuari si és necessari
        if (fid != null && !fid.trim().isEmpty()) {
            associateDeviceWithUser(fid, usuari.getId(), appVersion);
        }

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

/**
 * Mètode auxiliar per associar un dispositiu amb un usuari durant el login
 */
private void associateDeviceWithUser(String fid, Long userId, String appVersion) {
    try {
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);
        
        if (registration == null) {
            logger.info("Dispositiu {} no registrat. S'ignorarà l'associació.", fid);
            return;
        }
        
        // Si el dispositiu ja té un usuari associat, no el canviem
        if (registration.getUserId() != null) {
            if (!registration.getUserId().equals(userId)) {
                logger.warn("Dispositiu {} ja està associat amb un altre usuari ({}). No es canviarà.", fid, registration.getUserId());
            }
            return;
        }
        
        // Associar l'usuari al dispositiu
        registration.setUserId(userId);
        if (appVersion != null) {
            registration.setAppVersion(appVersion);
        }
        
        deviceRepository.save(registration);
        logger.info("Dispositiu {} associat amb usuari {} correctament.", fid, userId);
        
    } catch (Exception e) {
        logger.error("Error associant dispositiu {} amb usuari {}: {}", fid, userId, e.getMessage());
        // No llençem l'error perquè el login ha de continuar encara que falli l'associació
    }
}

}
