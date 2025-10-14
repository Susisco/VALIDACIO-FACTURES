package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository.AppVersionCount;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;
import cat.ajterrassa.validaciofactures.model.Usuari;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceRegistrationController {

    @Autowired
    private DeviceRegistrationRepository deviceRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @PostMapping("/devices/register")
    public ResponseEntity<?> registerDevice(@RequestBody FidRequest request,
                                            @RequestHeader(value = "X-App-Version", required = false) String appVersion) {
        // Registre inicial del dispositiu sense usuari (anònim)
        DeviceRegistration registration = deviceRepository.findByFid(request.getFid())
                .orElse(DeviceRegistration.builder()
                        .fid(request.getFid())
                        .status(DeviceRegistrationStatus.PENDING)
                        .build());
        
        // Actualitzar app version si es proporciona
        if (appVersion != null) {
            registration.setAppVersion(appVersion);
        }
        
        deviceRepository.save(registration);
        return ResponseEntity.ok(registration.getStatus());
    }

    @PostMapping("/devices/associate-user")
    public ResponseEntity<?> associateUserToDevice(@RequestBody FidRequest request,
                                                   @RequestHeader(value = "X-App-Version", required = false) String appVersion,
                                                   Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Usuari user = usuariRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DeviceRegistration registration = deviceRepository.findByFid(request.getFid())
                .orElseThrow(() -> new RuntimeException("Device not registered"));

        // Associar usuari al dispositiu
        registration.setUserId(user.getId());
        if (appVersion != null) {
            registration.setAppVersion(appVersion);
        }
        
        deviceRepository.save(registration);
        return ResponseEntity.ok(Map.of("message", "Device associated with user successfully"));
    }

    @GetMapping("/devices/versions")
    public List<AppVersionCount> deviceVersions() {
        return deviceRepository.countByAppVersion();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/admin/devices")
    public List<DeviceRegistration> listDevices() {
        return deviceRepository.findAll();
    }

    @PostMapping("/admin/devices/{fid}/approve")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> approveDevice(@PathVariable String fid) {
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);
        if (registration == null) {
            return ResponseEntity.notFound().build();
        }
        registration.setStatus(DeviceRegistrationStatus.APPROVED);
        deviceRepository.save(registration);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/devices/{fid}/revoke")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> revokeDevice(@PathVariable String fid) {
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);
        if (registration == null) {
            return ResponseEntity.notFound().build();
        }
        registration.setStatus(DeviceRegistrationStatus.REVOKED);
        deviceRepository.save(registration);
        return ResponseEntity.ok().build();
    }

    public static class FidRequest {
        private String fid;

        public String getFid() {
            return fid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }
    }

    // DTO per resposta d'estat del dispositiu
    public static class DeviceStatusResponse {
        private String status;
        private String message;
        private boolean canLogin;

        public DeviceStatusResponse(String status, String message, boolean canLogin) {
            this.status = status;
            this.message = message;
            this.canLogin = canLogin;
        }

        // Getters
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public boolean isCanLogin() { return canLogin; }
    }

    @GetMapping("/devices/status")
    public ResponseEntity<DeviceStatusResponse> getDeviceStatus(
            @RequestHeader(value = "X-Firebase-Installation-Id", required = true) String fid) {
        
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);
        
        if (registration == null) {
            return ResponseEntity.ok(new DeviceStatusResponse(
                "NOT_REGISTERED",
                "Dispositiu no registrat. Registrant automàticament...",
                false
            ));
        }
        
        DeviceRegistrationStatus status = registration.getStatus();
        if (status == DeviceRegistrationStatus.PENDING) {
            return ResponseEntity.ok(new DeviceStatusResponse(
                "PENDING",
                "El teu dispositiu està pendent d'aprovació per part dels administradors. Si us plau, contacta amb l'administrador del sistema.",
                false
            ));
        } else if (status == DeviceRegistrationStatus.APPROVED) {
            return ResponseEntity.ok(new DeviceStatusResponse(
                "APPROVED",
                "Dispositiu aprovat. Pots fer login.",
                true
            ));
        } else if (status == DeviceRegistrationStatus.REVOKED) {
            return ResponseEntity.ok(new DeviceStatusResponse(
                "REVOKED",
                "El teu dispositiu ha estat revocat pels administradors. Si us plau, contacta amb l'administrador del sistema.",
                false
            ));
        } else {
            return ResponseEntity.ok(new DeviceStatusResponse(
                "UNKNOWN",
                "Estat del dispositiu desconegut.",
                false
            ));
        }
    }

    // DTO per informació completa del dispositiu (per Settings)
    public static class DeviceInfoResponse {
        private String fid;
        private String status;
        private String appVersion;
        private String associatedUser;
        private java.time.LocalDateTime registrationDate;

        public DeviceInfoResponse(String fid, String status, String appVersion, 
                                 String associatedUser, java.time.LocalDateTime registrationDate) {
            this.fid = fid;
            this.status = status;
            this.appVersion = appVersion;
            this.associatedUser = associatedUser;
            this.registrationDate = registrationDate;
        }

        // Getters
        public String getFid() { return fid; }
        public String getStatus() { return status; }
        public String getAppVersion() { return appVersion; }
        public String getAssociatedUser() { return associatedUser; }
        public java.time.LocalDateTime getRegistrationDate() { return registrationDate; }
    }

    @GetMapping("/devices/my-info")
    public ResponseEntity<DeviceInfoResponse> getMyDeviceInfo(
            @RequestHeader(value = "X-Firebase-Installation-Id", required = true) String fid) {
        
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);
        
        if (registration == null) {
            return ResponseEntity.notFound().build();
        }
        
        String associatedUser = null;
        if (registration.getUserId() != null) {
            // Obtenir usuari per ID
            Usuari usuari = usuariRepository.findById(registration.getUserId()).orElse(null);
            if (usuari != null) {
                associatedUser = usuari.getEmail();
            }
        }
        
        DeviceInfoResponse response = new DeviceInfoResponse(
            registration.getFid(),
            registration.getStatus().name(),
            registration.getAppVersion(),
            associatedUser,
            null // Temporalment null fins que afegim timestamps al model
        );
        
        return ResponseEntity.ok(response);
    }
}
