package cat.ajterrassa.validaciofactures.service;

import cat.ajterrassa.validaciofactures.model.HistoricCanvi;
import cat.ajterrassa.validaciofactures.model.Usuari;
import cat.ajterrassa.validaciofactures.repository.HistoricCanviRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class HistoricCanviService {

    @Autowired
    private HistoricCanviRepository historicCanviRepository;

    public void registrarCanvi(String tipusDocument, Long documentId, Usuari usuari, String descripcio) {
        HistoricCanvi canvi = new HistoricCanvi();
        canvi.setTipusDocument(tipusDocument);
        canvi.setDocumentId(documentId);
        canvi.setUsuari(usuari);
        canvi.setDescripcio(descripcio);
        canvi.setDataHora(Instant.now());

        historicCanviRepository.save(canvi);
    }

    public List<HistoricCanvi> findByTipusAndDocumentId(String tipus, Long id) {
        return historicCanviRepository.findByTipusDocumentAndDocumentIdOrderByDataHoraDesc(tipus, id);
    }

}
