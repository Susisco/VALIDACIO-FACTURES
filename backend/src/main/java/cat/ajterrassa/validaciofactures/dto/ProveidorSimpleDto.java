package cat.ajterrassa.validaciofactures.dto;

public class ProveidorSimpleDto {
    private Long id;
    private String nomComercial;

    public ProveidorSimpleDto() {
    }

    public ProveidorSimpleDto(Long id, String nomComercial) {
        this.id = id;
        this.nomComercial = nomComercial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomComercial() {
        return nomComercial;
    }

    public void setNomComercial(String nomComercial) {
        this.nomComercial = nomComercial;
    }
}
