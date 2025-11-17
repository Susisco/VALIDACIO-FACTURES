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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceRegistrationController {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private DeviceRegistrationRepository deviceRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @PostMapping("/devices/register")
    public ResponseEntity<?> registerDevice(@RequestBody FidRequest request,
                                            @RequestHeader(value = "X-App-Version", required = false) String appVersion) {
        System.out.println("📱 registerDevice called with FID: " + request.getFid() + ", AppVersion: " + appVersion);
        
        // Registre inicial del dispositiu sense usuari (anònim)
        DeviceRegistration registration = deviceRepository.findByFid(request.getFid())
                .orElse(DeviceRegistration.builder()
                        .fid(request.getFid())
                        .status(DeviceRegistrationStatus.PENDING)
                        .build());
        
        System.out.println("📋 Registration status before save: " + registration.getStatus());
        
        // Actualitzar app version si es proporciona
        if (appVersion != null) {
            registration.setAppVersion(appVersion);
        }
        // Marquem darrera activitat en registre
        registration.setLastSeenAt(Instant.now());
        
        deviceRepository.save(registration);
        System.out.println("💾 Device saved with status: " + registration.getStatus());
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
        registration.setLastSeenAt(Instant.now());
        
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
        private String deviceId;
        private String deviceInfo;
        private String registeredAt;

        public DeviceStatusResponse(String status, String message, boolean canLogin) {
            this.status = status;
            this.message = message;
            this.canLogin = canLogin;
            this.deviceId = null;
            this.deviceInfo = null;
            this.registeredAt = null;
        }

        public DeviceStatusResponse(String status, String message, boolean canLogin, 
                                  String deviceId, String deviceInfo, String registeredAt) {
            this.status = status;
            this.message = message;
            this.canLogin = canLogin;
            this.deviceId = deviceId;
            this.deviceInfo = deviceInfo;
            this.registeredAt = registeredAt;
        }

        // Getters
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public boolean isCanLogin() { return canLogin; }
        public String getDeviceId() { return deviceId; }
        public String getDeviceInfo() { return deviceInfo; }
        public String getRegisteredAt() { return registeredAt; }
    }

    @GetMapping("/devices/status")
    public ResponseEntity<DeviceStatusResponse> getDeviceStatus(
            @RequestHeader(value = "X-Firebase-Installation-Id", required = true) String fid) {
        
        System.out.println("🔍 getDeviceStatus called with FID: " + fid);
        
        DeviceRegistration registration = deviceRepository.findByFid(fid).orElse(null);

        if (registration == null) {
            System.out.println("❌ No registration found for FID: " + fid + " → creating PENDING record");
            // Autocreació del registre perquè l'app tingui estat coherent immediatament
            registration = DeviceRegistration.builder()
                    .fid(fid)
                    .status(DeviceRegistrationStatus.PENDING)
                    .build();
            registration.setLastSeenAt(Instant.now());
            deviceRepository.saveAndFlush(registration);

            System.out.println("💾 Created device with status PENDING for FID: " + fid);
                String createdAtStr = formatInstant(registration.getCreatedAt());
            return ResponseEntity.ok(new DeviceStatusResponse(
                "PENDING",
                "Dispositiu registrat i pendent d'aprovació.",
                false,
                fid,
                "Dispositiu Android",
                createdAtStr
            ));
        }
        
        System.out.println("✅ Registration found - Status: " + registration.getStatus() + 
                          ", UserID: " + registration.getUserId() + 
                          ", AppVersion: " + registration.getAppVersion());
        // Marquem darrera activitat quan consulta status
        registration.setLastSeenAt(Instant.now());
        deviceRepository.save(registration);
        
        // Preparar informació del dispositiu amb camps disponibles
        String deviceId = registration.getFid();
        String deviceInfo = registration.getAppVersion() != null ? 
            "Versió " + registration.getAppVersion() : "Dispositiu Android";
    String registeredAt = formatInstant(registration.getCreatedAt());
        
        DeviceRegistrationStatus status = registration.getStatus();
        if (status == DeviceRegistrationStatus.PENDING) {
            System.out.println("📋 Returning PENDING status for FID: " + fid);
            return ResponseEntity.ok(new DeviceStatusResponse(
                "PENDING",
                "El teu dispositiu està pendent d'aprovació per part dels administradors. Si us plau, contacta amb l'administrador del sistema.",
                false,
                deviceId,
                deviceInfo,
                registeredAt
            ));
        } else if (status == DeviceRegistrationStatus.APPROVED) {
            System.out.println("✅ Returning APPROVED status for FID: " + fid);
            return ResponseEntity.ok(new DeviceStatusResponse(
                "APPROVED",
                "Dispositiu aprovat. Pots fer login.",
                true,
                deviceId,
                deviceInfo,
                registeredAt
            ));
        } else if (status == DeviceRegistrationStatus.REVOKED) {
            System.out.println("❌ Returning REVOKED status for FID: " + fid);
            return ResponseEntity.ok(new DeviceStatusResponse(
                "REVOKED",
                "El teu dispositiu ha estat revocat pels administradors. Si us plau, contacta amb l'administrador del sistema.",
                false,
                deviceId,
                deviceInfo,
                registeredAt
            ));
        } else if (status == DeviceRegistrationStatus.ARCHIVED) {
            System.out.println("📦 Returning ARCHIVED status for FID: " + fid);
            return ResponseEntity.ok(new DeviceStatusResponse(
                "ARCHIVED",
                "El teu dispositiu ha estat arxivat per inactivitat. Contacta amb l'administrador per reactivar-lo.",
                false,
                deviceId,
                deviceInfo,
                registeredAt
            ));
        } else if (status == DeviceRegistrationStatus.DELETED) {
            System.out.println("🗑️ Returning DELETED status for FID: " + fid);
            return ResponseEntity.ok(new DeviceStatusResponse(
                "DELETED",
                "El teu dispositiu té baixa lògica. Contacta amb l'administrador si s'ha de reactivar.",
                false,
                deviceId,
                deviceInfo,
                registeredAt
            ));
        } else {
            return ResponseEntity.ok(new DeviceStatusResponse(
                "UNKNOWN",
                "Estat del dispositiu desconegut.",
                false,
                deviceId,
                deviceInfo,
                registeredAt
            ));
        }
    }

    // DTO per informació completa del dispositiu (per Settings)
    public static class DeviceInfoResponse {
        private String fid;
        private String status;
        private String appVersion;
        private String associatedUser;
        private Instant registrationDate;

        public DeviceInfoResponse(String fid, String status, String appVersion, 
                     String associatedUser, Instant registrationDate) {
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
        public Instant getRegistrationDate() { return registrationDate; }
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
            registration.getCreatedAt()
        );
        
        return ResponseEntity.ok(response);
    }

    private String formatInstant(Instant instant) {
        return instant != null ? ISO_FORMATTER.format(instant) : null;
    }
}
