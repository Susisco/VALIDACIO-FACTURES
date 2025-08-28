package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.AlbaraDetallMatch;
import cat.ajterrassa.validaciofactures.model.Albara;
import cat.ajterrassa.validaciofactures.model.FacturaDetall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbaraDetallMatchRepository extends JpaRepository<AlbaraDetallMatch, Long> {

    Optional<AlbaraDetallMatch> findByAlbara(Albara albara);

    Optional<AlbaraDetallMatch> findByDetall(FacturaDetall detall);

    List<AlbaraDetallMatch> findByAlbaraIn(List<Albara> albarans);

    List<AlbaraDetallMatch> findByDetallIn(List<FacturaDetall> detalls);

    boolean existsByAlbara(Albara albara);

    boolean existsByDetall(FacturaDetall detall);

    void deleteByAlbara(Albara albara);

    void deleteByDetall(FacturaDetall detall);
}
