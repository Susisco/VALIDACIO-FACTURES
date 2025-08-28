package cat.ajterrassa.validaciofactures.dto;

import lombok.Getter;
import lombok.Setter;

// src/main/java/cat/ajterrassa/validaciofactures/dto/ProveidorDto.java

@Getter @Setter
public class ProveidorDto {
    private Long id;
    private String nomComercial;
    private String nom;
    private String nif;
    private String adreca;
    // getters + setters
    public Long getId() {
        return id;
    }
 }
