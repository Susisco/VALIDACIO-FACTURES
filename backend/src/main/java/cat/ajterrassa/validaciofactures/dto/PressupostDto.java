package cat.ajterrassa.validaciofactures.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PressupostDto {
    private String tipus;
    private String referenciaDocument;
    private LocalDate data;
    private BigDecimal importTotal;
    private String estat;
    private Long creadorId;
    private Long validatPerId;
    private Long proveidorId;
    private String fitxerAdjunt;
    private Long edificiId;
    private Long otsId;
    private Long facturaId;

    public String getTipus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
