package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.Edifici;
import cat.ajterrassa.validaciofactures.repository.EdificiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EdificiService {
    private final EdificiRepository repo;

    public EdificiService(EdificiRepository repo) {
        this.repo = repo;
    }

    public List<Edifici> findAll() {
        return repo.findAll();
    }

    public Optional<Edifici> findById(Long id) {
        return repo.findById(id);
    }

    public Edifici save(Edifici e) {
        return repo.save(e);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
