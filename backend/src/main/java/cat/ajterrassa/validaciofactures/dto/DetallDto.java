package cat.ajterrassa.validaciofactures.dto;

import java.math.BigDecimal;

public class DetallDto {

    private Long id;
    private String referenciaDocumentDetall;
    private BigDecimal importTotalDetall;
    private Long facturaId; // opcional, no cal si ja es vincula per backend
    private Long albaraRelacionatId;
    // NOUS CAMPS
    private String referenciaAlbaraRelacionat;
    private BigDecimal importAlbaraRelacionat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenciaDocumentDetall() {
        return referenciaDocumentDetall;
    }

    public void setReferenciaDocumentDetall(String referenciaDocumentDetall) {
        this.referenciaDocumentDetall = referenciaDocumentDetall;
    }

public BigDecimal getImportTotalDetall() {
    return importTotalDetall;
}

public void setImportTotalDetall(BigDecimal importTotalDetall) {
    this.importTotalDetall = importTotalDetall;
}


    public Long getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }

    public Long getAlbaraRelacionatId() {
        return albaraRelacionatId;
    }

    public void setAlbaraRelacionatId(Long albaraRelacionatId) {
        this.albaraRelacionatId = albaraRelacionatId;
    }


public String getReferenciaAlbaraRelacionat() {
        return referenciaAlbaraRelacionat;
    }
public void setReferenciaAlbaraRelacionat(String referenciaAlbaraRelacionat) {
        this.referenciaAlbaraRelacionat = referenciaAlbaraRelacionat;
    }
public BigDecimal getImportAlbaraRelacionat() {
        return importAlbaraRelacionat;
    }
public void setImportAlbaraRelacionat(BigDecimal importAlbaraRelacionat) {
        this.importAlbaraRelacionat = importAlbaraRelacionat;

}

}
