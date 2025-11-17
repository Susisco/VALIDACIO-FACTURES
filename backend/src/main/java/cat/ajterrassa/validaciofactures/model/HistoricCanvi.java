package cat.ajterrassa.validaciofactures.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "historic_canvis")
public class HistoricCanvi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipusDocument;
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "usuari_id")
    private Usuari usuari;

    private String descripcio;
    private Instant dataHora;

    // Getters i setters
    public Long getId() {
        return id;
    }

    public String getTipusDocument() {
        return tipusDocument;
    }

    public void setTipusDocument(String tipusDocument) {
        this.tipusDocument = tipusDocument;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Usuari getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuari usuari) {
        this.usuari = usuari;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public Instant getDataHora() {
        return dataHora;
    }

    public void setDataHora(Instant dataHora) {
        this.dataHora = dataHora;
    }
}
