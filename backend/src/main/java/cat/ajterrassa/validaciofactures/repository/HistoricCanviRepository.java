package cat.ajterrassa.validaciofactures.repository;

import cat.ajterrassa.validaciofactures.model.HistoricCanvi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricCanviRepository extends JpaRepository<HistoricCanvi, Long> {
    List<HistoricCanvi> findByTipusDocumentAndDocumentId(String tipusDocument, Long documentId);

    //cerca documents per tipus i id document, ordenats per data i hora descendent
    List<HistoricCanvi> findByTipusDocumentAndDocumentIdOrderByDataHoraDesc(String tipusDocument, Long documentId);

    //esborra l'historial d'un document per tipus i id, es fa servir quan eliminem un document, per exemple, un albarà
    void deleteByDocumentIdAndTipusDocument(Long documentId, String tipusDocument);

}

