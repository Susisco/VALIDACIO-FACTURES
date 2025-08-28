package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.Pressupost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PressupostRepository extends JpaRepository<Pressupost, Long> {

            // troba pressupostos lliures del mateix proveïdor
    List<Pressupost> findByFacturaIsNullAndProveidorId(Long proveidorId);


    // troba pressupostos lliures del mateix proveïdor amb la mateixa referència
    List<Pressupost> findByFacturaIsNullAndProveidorIdAndReferenciaDocumentIn(
        Long proveidorId,
        List<String> referencias
    );
}
