package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.Albara;
import cat.ajterrassa.validaciofactures.model.AlbaraDetallMatch;
import cat.ajterrassa.validaciofactures.model.FacturaDetall;
import cat.ajterrassa.validaciofactures.repository.AlbaraDetallMatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlbaraDetallMatchService {

    private final AlbaraDetallMatchRepository matchRepository;

    public AlbaraDetallMatchService(AlbaraDetallMatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Optional<AlbaraDetallMatch> getByAlbara(Albara albara) {
        return matchRepository.findByAlbara(albara);
    }

    public Optional<AlbaraDetallMatch> getByDetall(FacturaDetall detall) {
        return matchRepository.findByDetall(detall);
    }

    public List<AlbaraDetallMatch> getByAlbarans(List<Albara> albarans) {
        return matchRepository.findByAlbaraIn(albarans);
    }

    public List<AlbaraDetallMatch> getByDetalls(List<FacturaDetall> detalls) {
        return matchRepository.findByDetallIn(detalls);
    }

    public boolean existsForAlbara(Albara albara) {
        return matchRepository.existsByAlbara(albara);
    }

    public boolean existsForDetall(FacturaDetall detall) {
        return matchRepository.existsByDetall(detall);
    }

    public AlbaraDetallMatch crearRelacio(Albara albara, FacturaDetall detall) {
        if (existsForAlbara(albara)) {
            throw new IllegalStateException("L'albarà ja està relacionat amb una línia de factura");
        }
        if (existsForDetall(detall)) {
            throw new IllegalStateException("La línia de factura ja està relacionada amb un albarà");
        }

        AlbaraDetallMatch match = new AlbaraDetallMatch();
        match.setAlbara(albara);
        match.setDetall(detall);
        return matchRepository.save(match);
    }

    public void eliminarRelacioPerAlbara(Albara albara) {
        matchRepository.deleteByAlbara(albara);
    }

    public void eliminarRelacioPerDetall(FacturaDetall detall) {
        matchRepository.deleteByDetall(detall);
    }
}
