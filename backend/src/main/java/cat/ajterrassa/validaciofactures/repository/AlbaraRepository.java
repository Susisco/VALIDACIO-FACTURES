// src/main/java/cat/ajterrassa/validaciofactures/repository/AlbaraRepository.java
package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.Albara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface AlbaraRepository extends JpaRepository<Albara, Long> {

    // troba albarans lliures del mateix proveïdor
    List<Albara> findByFacturaIsNullAndProveidorId(Long proveidorId);

    // troba albarans lliures del mateix proveïdor amb les mateixes referències
    List<Albara> findByFacturaIsNullAndProveidorIdAndReferenciaDocumentIn(
            Long proveidorId,
            List<String> referencias
    );

//el fem servir a l'app per cercar albarans de l'usuari
    @Query("SELECT a FROM Albara a WHERE a.creador.email = :email")
    List<Albara> findByCreadorEmail(@Param("email") String email);

    List<Albara> findByFacturaId(Long facturaId);

}
