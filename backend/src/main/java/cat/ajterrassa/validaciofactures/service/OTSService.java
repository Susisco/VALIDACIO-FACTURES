package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.OTS;
import cat.ajterrassa.validaciofactures.repository.OTSRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OTSService {
    private final OTSRepository repo;

    public OTSService(OTSRepository repo) {
        this.repo = repo;
    }

    public List<OTS> findAll() {
        return repo.findAll();
    }

    public Optional<OTS> findById(Long id) {
        return repo.findById(id);
    }

    public OTS save(OTS ots) {
        return repo.save(ots);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
