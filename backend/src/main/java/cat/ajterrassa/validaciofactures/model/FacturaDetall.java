package cat.ajterrassa.validaciofactures.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class FacturaDetall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_total_detall", precision = 38, scale = 2)
    private BigDecimal importTotalDetall;

    @Column(name = "referencia_document_detall", length = 255, unique = true)
    private String referenciaDocumentDetall;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonBackReference
    private Factura factura;

    public FacturaDetall() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getImportTotalDetall() {
        return importTotalDetall;
    }

    public void setImportTotalDetall(BigDecimal importTotalDetall) {
        this.importTotalDetall = importTotalDetall;
    }

    public String getReferenciaDocumentDetall() {
        return referenciaDocumentDetall;
    }

    public void setReferenciaDocumentDetall(String referenciaDocumentDetall) {
        this.referenciaDocumentDetall = referenciaDocumentDetall;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
}
