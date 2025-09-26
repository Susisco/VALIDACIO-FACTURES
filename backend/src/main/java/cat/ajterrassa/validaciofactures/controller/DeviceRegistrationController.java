package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository.AppVersionCount;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;
import cat.ajterrassa.validaciofactures.model.Usuari;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DeviceRegistrationController {

    @Autowired
    private DeviceRegistrationRepository deviceRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @PostMapping("/devices/register")
    public ResponseEntity<?> registerDevice(@RequestBody FidRequest request,
                                            @RequestHeader(value = "X-App-Version", required = false) String appVersion,
                                            Principal principal) {
        Long userId = null;
        if (principal != null) {
            Usuari user = usuariRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                userId = user.getId();
            }
        }
        DeviceRegistration registration = deviceRepository.findByFid(request.getFid())
                .orElse(DeviceRegistration.builder()
                        .fid(request.getFid())
                        .status(DeviceRegistrationStatus.PENDING)
                        .build());
        registration.setUserId(userId);
        registration.setAppVersion(appVersion);
        deviceRepository.save(registration);
        return ResponseEntity.ok(registration.getStatus());
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
}
