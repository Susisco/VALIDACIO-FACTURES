package cat.ajterrassa.validaciofactures.dto;

public class AlbaraCreateDto {

    private String tipus;
    private String referenciaDocument;
    private String data; // String per convertir després a LocalDate
    private double importTotal;
    private String estat;
    private Long creadorId;
    private Long validatPerId;
    private Long proveidorId;
    private Long edificiId;
    private Long otsId;
    private Long facturaId;
    private String fitxerAdjunt;
    private Long usuariModificacioId;
    private String usuariModificacioNom;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getImportTotal() {
        return importTotal;
    }

    public void setImportTotal(double importTotal) {
        this.importTotal = importTotal;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
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

    public Long getUsuariModificacioId() {
        return usuariModificacioId;
    }

    public void setUsuariModificacioId(Long usuariModificacioId) {
        this.usuariModificacioId = usuariModificacioId;
    }

    public String getUsuariModificacioNom() {
        return usuariModificacioNom;
    }

    public void setUsuariModificacioNom(String usuariModificacioNom) {
        this.usuariModificacioNom = usuariModificacioNom;
    }
}
