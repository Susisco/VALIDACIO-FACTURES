package cat.ajterrassa.validaciofactures.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;

public interface DeviceRegistrationRepository extends JpaRepository<DeviceRegistration, Long> {
    Optional<DeviceRegistration> findByFid(String fid);

    @Query("SELECT dr.appVersion AS appVersion, COUNT(dr) AS count FROM DeviceRegistration dr GROUP BY dr.appVersion")
    List<AppVersionCount> countByAppVersion();

    interface AppVersionCount {
        String getAppVersion();
        Long getCount();
    }
}
