package cat.ajterrassa.validaciofactures.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;

public interface DeviceRegistrationRepository extends JpaRepository<DeviceRegistration, Long> {
    Optional<DeviceRegistration> findByFid(String fid);

    @Query("SELECT dr.appVersion AS appVersion, COUNT(dr) AS count FROM DeviceRegistration dr GROUP BY dr.appVersion")
    List<AppVersionCount> countByAppVersion();

    // Suport per neteja i canvis d'estat per inactivitat
    List<DeviceRegistration> findAllByStatusAndLastSeenAtBefore(DeviceRegistrationStatus status, LocalDateTime threshold);
    List<DeviceRegistration> findAllByStatusAndArchivedAtBefore(DeviceRegistrationStatus status, LocalDateTime threshold);

    interface AppVersionCount {
        String getAppVersion();
        Long getCount();
    }
}
