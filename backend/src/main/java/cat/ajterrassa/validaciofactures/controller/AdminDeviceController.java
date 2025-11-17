package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/devices")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminDeviceController {

    private final DeviceRegistrationRepository repo;

    public AdminDeviceController(DeviceRegistrationRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/{fid}/reactivate")
    public ResponseEntity<?> reactivate(@PathVariable String fid) {
        return repo.findByFid(fid).map(reg -> {
            reg.setStatus(DeviceRegistrationStatus.APPROVED);
            reg.setArchivedAt(null);
            reg.setDeletedAt(null);
            repo.save(reg);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{fid}/archive")
    public ResponseEntity<?> archive(@PathVariable String fid) {
        return repo.findByFid(fid).map(reg -> {
            reg.setStatus(DeviceRegistrationStatus.ARCHIVED);
            reg.setArchivedAt(Instant.now());
            repo.save(reg);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{fid}/delete-logical")
    public ResponseEntity<?> deleteLogical(@PathVariable String fid) {
        return repo.findByFid(fid).map(reg -> {
            reg.setStatus(DeviceRegistrationStatus.DELETED);
            reg.setDeletedAt(Instant.now());
            repo.save(reg);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
