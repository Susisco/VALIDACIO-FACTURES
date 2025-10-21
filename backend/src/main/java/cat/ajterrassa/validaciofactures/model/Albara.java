package cat.ajterrassa.validaciofactures.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Albara extends DocumentUbicacio {

    @ManyToOne @JoinColumn(name = "factura_id")
    private Factura factura;

    // Identificador de enviament idempotent, generat per l'app (UUID)
    @Column(name = "submission_id", unique = true)
    private String submissionId;

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

}
