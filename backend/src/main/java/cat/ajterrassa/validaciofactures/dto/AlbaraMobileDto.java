package cat.ajterrassa.validaciofactures.dto;

import cat.ajterrassa.validaciofactures.model.DocumentBase.EstatDocument;

public class AlbaraMobileDto {
    private Long id;
    private String referenciaDocument;
    private String data;
    private EstatDocument estat;
    private String importTotal;
    private String dataCreacio;

    public AlbaraMobileDto(Long id, String referenciaDocument, String data, EstatDocument estat, String importTotal, String dataCreacio) {
        this.id = id;
        this.referenciaDocument = referenciaDocument;
        this.data = data;
        this.estat = estat;
        this.importTotal = importTotal;
        this.dataCreacio = dataCreacio;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public EstatDocument getEstat() {
        return estat;
    }

    public void setEstat(EstatDocument estat) {
        this.estat = estat;
    }
    public String getImportTotal() {
        return importTotal;
    }
    public void setImportTotal(String importTotal) {
        this.importTotal = importTotal;
    }
 
    public String getDataCreacio() {
        return dataCreacio;
    }
    public void setDataCreacio(String dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

}