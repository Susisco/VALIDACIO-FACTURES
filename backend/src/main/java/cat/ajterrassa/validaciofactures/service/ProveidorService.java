package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.Proveidor;
import cat.ajterrassa.validaciofactures.repository.ProveidorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveidorService {

    private final ProveidorRepository repo;
    

    public ProveidorService(ProveidorRepository repo) {
        this.repo = repo;
    }

    public List<Proveidor> findAll() {
        return repo.findAll();
    }


    /** Retorna tots o, si search present, els que continguin la cadena. */
    //modificat perque serveixi per a la cerca de proveidors al formulari de creacio de factures
        public List<Proveidor> findAll(String search) {
        if (search == null || search.isBlank()) {
            return repo.findAll();
        }
        return repo.findByNomComercialContainingIgnoreCaseOrNomContainingIgnoreCase(search, search);
    }

    
    public Optional<Proveidor> findById(Long id) {
        return repo.findById(id);
    }

    public Proveidor save(Proveidor p) {
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }


    /** Mètode nou: Comprova si ja existeix un proveïdor pel seu NIF */
    public boolean existsByNif(String nif) {
        return repo.existsByNif(nif);
    }
}
