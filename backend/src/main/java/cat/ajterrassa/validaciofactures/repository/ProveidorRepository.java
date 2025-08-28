package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.Proveidor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveidorRepository extends JpaRepository<Proveidor, Long> {


     // mètode per cerca per nom comercial o nom
List<Proveidor> findByNomComercialContainingIgnoreCaseOrNomContainingIgnoreCase(String comercial,String nom);

boolean existsByNif(String nif);

}
