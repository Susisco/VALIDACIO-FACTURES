package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    
    Optional<Usuari> findByEmail(String email);






}