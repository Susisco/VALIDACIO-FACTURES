package cat.ajterrassa.validaciofactures.dto;

import java.math.BigDecimal;

public class DetallMatchDTO {
    private Long detallId;
    private String referenciaDocumentDetall;
    private BigDecimal importTotalDetall;
    private Long albaraRelacionatId;


    public DetallMatchDTO() {}

    public DetallMatchDTO(Long detallId, String referenciaDocumentDetall, BigDecimal importTotalDetall) {
        this.detallId = detallId;
        this.referenciaDocumentDetall = referenciaDocumentDetall;
        this.importTotalDetall = importTotalDetall;
    }

    public Long getDetallId() {
        return detallId;
    }

    public void setDetallId(Long detallId) {
        this.detallId = detallId;
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

    public Long getAlbaraRelacionatId() {
        return albaraRelacionatId;
    }

    public void setAlbaraRelacionatId(Long albaraRelacionatId) {
        this.albaraRelacionatId = albaraRelacionatId;
    }

}
