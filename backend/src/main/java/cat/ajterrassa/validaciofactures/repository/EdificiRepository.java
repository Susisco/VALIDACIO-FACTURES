package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.Edifici;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdificiRepository extends JpaRepository<Edifici, Long> {
}
