package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> { }
