package cat.ajterrassa.validaciofactures.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import cat.ajterrassa.validaciofactures.model.DocumentBase.EstatDocument;

public class AlbaraDto {
    private String tipus;
    private String referenciaDocument;
    private LocalDate data;
    private BigDecimal importTotal;
    private EstatDocument estat; // Cambiado a EstatDocument
    private Long creadorId;
    private Long validatPerId;    // Puede ser null
    private Long proveidorId;
    private Long edificiId;
    private Long otsId;
    private Long facturaId;       // Puede ser null
    private String fitxerAdjunt;
private UsuariSimpleDto usuariModificacio;

    // Getters y setters
    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public String getReferenciaDocument() {
        return referenciaDocument;
    }

    public void setReferenciaDocument(String referenciaDocument) {
        this.referenciaDocument = referenciaDocument;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getImportTotal() {
        return importTotal;
    }

    public void setImportTotal(BigDecimal importTotal) {
        this.importTotal = importTotal;
    }

    public EstatDocument getEstat() {
        return estat;
    }

    public void setEstat(EstatDocument estat) {
        this.estat = estat;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }

    public Long getValidatPerId() {
        return validatPerId;
    }

    public void setValidatPerId(Long validatPerId) {
        this.validatPerId = validatPerId;
    }

    public Long getProveidorId() {
        return proveidorId;
    }

    public void setProveidorId(Long proveidorId) {
        this.proveidorId = proveidorId;
    }

    public Long getEdificiId() {
        return edificiId;
    }

    public void setEdificiId(Long edificiId) {
        this.edificiId = edificiId;
    }

    public Long getOtsId() {
        return otsId;
    }

    public void setOtsId(Long otsId) {
        this.otsId = otsId;
    }

    public Long getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }

    public String getFitxerAdjunt() {
        return fitxerAdjunt;
    }

    public void setFitxerAdjunt(String fitxerAdjunt) {
        this.fitxerAdjunt = fitxerAdjunt;
    }

    // Métodos para manejar el usuario de modificación
public UsuariSimpleDto getUsuariModificacio() {
    return usuariModificacio;
}

public void setUsuariModificacio(UsuariSimpleDto usuariModificacio) {
    this.usuariModificacio = usuariModificacio;
}


}
