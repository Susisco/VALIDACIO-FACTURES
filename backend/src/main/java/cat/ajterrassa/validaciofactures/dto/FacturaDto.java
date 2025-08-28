package cat.ajterrassa.validaciofactures.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FacturaDto {
    private Long id;
    private String tipus;
    private String referenciaDocument;
    private LocalDate data;
    private BigDecimal importTotal;
    private String estat;
    private UsuariDto creador;
    private UsuariDto validatPer;      // pot ser null
private ProveidorSimpleDto proveidor;
     private String fitxerAdjunt;       // pot ser null
    private LocalDateTime creat;       // pot ser null
    private LocalDateTime actualitzat;
    private List<DetallDto> detalls;
    private List<AlbaraSimpleDto> albaransRelacionats;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public UsuariDto getCreador() {
        return creador;
    }

    public void setCreador(UsuariDto creador) {
        this.creador = creador;
    }

    public UsuariDto getValidatPer() {
        return validatPer;
    }

    public void setValidatPer(UsuariDto validatPer) {
        this.validatPer = validatPer;
    }

    

    public ProveidorSimpleDto getProveidor() {
        return proveidor;
    }
    public void setProveidor(ProveidorSimpleDto proveidor) {
        this.proveidor = proveidor;
    }

    public String getFitxerAdjunt() {
        return fitxerAdjunt;
    }

    public void setFitxerAdjunt(String fitxerAdjunt) {
        this.fitxerAdjunt = fitxerAdjunt;
    }

    public LocalDateTime getCreat() {
        return creat;
    }

    public void setCreat(LocalDateTime creat) {
        this.creat = creat;
    }

    public LocalDateTime getActualitzat() {
        return actualitzat;
    }

    public void setActualitzat(LocalDateTime actualitzat) {
        this.actualitzat = actualitzat;
    }

    public List<DetallDto> getDetalls() {
        return detalls;
    }

    public void setDetalls(List<DetallDto> detalls) {
        this.detalls = detalls;
    }
    public List<AlbaraSimpleDto> getAlbaransRelacionats() {
        return albaransRelacionats;
    }
    public void setAlbaransRelacionats(List<AlbaraSimpleDto> albaransRelacionats) {
        this.albaransRelacionats = albaransRelacionats;
    }
}
