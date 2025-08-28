package cat.ajterrassa.validaciofactures.dto;

import java.math.BigDecimal;
import java.util.List;

public class AlbaraMatchDTO {
    private Long albaraId;
    private String referenciaAlbara;
    private BigDecimal importTotal;
    private List<DetallMatchDTO> liniesFacturesRelacionades;

    public AlbaraMatchDTO() {}

    public AlbaraMatchDTO(Long albaraId, String referenciaAlbara, BigDecimal importTotal, List<DetallMatchDTO> liniesFacturesRelacionades) {
        this.albaraId = albaraId;
        this.referenciaAlbara = referenciaAlbara;
        this.importTotal = importTotal;
        this.liniesFacturesRelacionades = liniesFacturesRelacionades;
    }

    public Long getAlbaraId() {
        return albaraId;
    }

    public void setAlbaraId(Long albaraId) {
        this.albaraId = albaraId;
    }

    public String getReferenciaAlbara() {
        return referenciaAlbara;
    }

    public void setReferenciaAlbara(String referenciaAlbara) {
        this.referenciaAlbara = referenciaAlbara;
    }

    public BigDecimal getImportTotal() {
        return importTotal;
    }

    public void setImportTotal(BigDecimal importTotal) {
        this.importTotal = importTotal;
    }

    public List<DetallMatchDTO> getLiniesFacturesRelacionades() {
        return liniesFacturesRelacionades;
    }

    public void setLiniesFacturesRelacionades(List<DetallMatchDTO> liniesFacturesRelacionades) {
        this.liniesFacturesRelacionades = liniesFacturesRelacionades;
    }
}
