package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class DeviceCleanupService {

    private final DeviceRegistrationRepository deviceRepository;

    public DeviceCleanupService(DeviceRegistrationRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Value("${devices.retention.archiveAfterDays:90}")
    private int archiveAfterDays;

    @Value("${devices.retention.deleteAfterDays:365}")
    private int deleteAfterDays;

    // Executa cada dia a les 03:30 (configurable)
    @Scheduled(cron = "${devices.cleanup.cron:0 30 3 * * *}")
    public void cleanupInactiveDevices() {
        Instant now = Instant.now();
        Instant archiveThreshold = now.minus(Duration.ofDays(archiveAfterDays));
        Instant deleteThreshold = now.minus(Duration.ofDays(deleteAfterDays));

        // 1) ARCHIVE: dispositius APPROVED o REVOKED sense activitat des de fa X dies
        for (DeviceRegistrationStatus st : new DeviceRegistrationStatus[]{DeviceRegistrationStatus.APPROVED, DeviceRegistrationStatus.REVOKED}) {
            List<DeviceRegistration> toArchive = deviceRepository.findAllByStatusAndLastSeenAtBefore(st, archiveThreshold);
            if (!toArchive.isEmpty()) {
                for (DeviceRegistration dr : toArchive) {
                    dr.setStatus(DeviceRegistrationStatus.ARCHIVED);
                    dr.setArchivedAt(now);
                }
                deviceRepository.saveAll(toArchive);
            }
        }

        // 2) DELETED lògic: dispositius ARCHIVED des de fa molt temps
        List<DeviceRegistration> toDeleteLogical = deviceRepository.findAllByStatusAndArchivedAtBefore(DeviceRegistrationStatus.ARCHIVED, deleteThreshold);
        if (!toDeleteLogical.isEmpty()) {
            for (DeviceRegistration dr : toDeleteLogical) {
                dr.setStatus(DeviceRegistrationStatus.DELETED);
                dr.setDeletedAt(now);
            }
            deviceRepository.saveAll(toDeleteLogical);
        }

        // 3) Opcional: PENDING sense activitat mai i molt antics → es poden arxivar directament
        // Nota: si preferim conservar tots els PENDING, podem saltar aquest pas.
        // Es pot afegir una propietat per activar/desactivar.
    }
}
