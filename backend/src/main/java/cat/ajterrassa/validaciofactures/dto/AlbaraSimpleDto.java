package cat.ajterrassa.validaciofactures.dto;

public class AlbaraSimpleDto {
    private Long id;
    private String referenciaDocument;

    public AlbaraSimpleDto() {}

    public AlbaraSimpleDto(Long id, String referenciaDocument) {
        this.id = id;
        this.referenciaDocument = referenciaDocument;
    }

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
}
