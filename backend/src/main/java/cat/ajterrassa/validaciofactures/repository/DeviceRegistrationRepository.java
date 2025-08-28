package cat.ajterrassa.validaciofactures.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;

public interface DeviceRegistrationRepository extends JpaRepository<DeviceRegistration, Long> {
    Optional<DeviceRegistration> findByFid(String fid);
}
