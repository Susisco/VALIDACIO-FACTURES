// FacturaDetallRepository.java
package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.FacturaDetall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaDetallRepository extends JpaRepository<FacturaDetall, Long> {
}
