package cat.ajterrassa.validaciofactures.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class DocumentBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipus;

    @Column(nullable = false, unique = true)
    private String referenciaDocument;

    @Column(name = "data")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    @Column(nullable = false)
    private BigDecimal importTotal;

    @Enumerated(EnumType.STRING)
    private EstatDocument estat;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuari creador;

    @ManyToOne
    @JoinColumn(name = "validat_per_id")
    private Usuari validatPer;

    @ManyToOne
    @JoinColumn(name = "proveidor_id", nullable = false)
    private Proveidor proveidor;

    private String fitxerAdjunt;

    private Instant creat;
    private Instant actualitzat;

    @ManyToOne
    @JoinColumn(name = "usuari_modificacio_id")
    private Usuari usuariModificacio;

    @PrePersist
    protected void onCreate() {
        this.creat = Instant.now();
        this.actualitzat = this.creat;
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualitzat = Instant.now();
    }

    public enum EstatDocument {
        PENDENT, EN_CURS, VALIDAT, REBUTJAT
    }

    // Getters y Setters
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

    public EstatDocument getEstat() {
        return estat;
    }

    public void setEstat(EstatDocument estat) {
        this.estat = estat;
    }

    public Usuari getCreador() {
        return creador;
    }

    public void setCreador(Usuari creador) {
        this.creador = creador;
    }

    public Usuari getValidatPer() {
        return validatPer;
    }

    public void setValidatPer(Usuari validatPer) {
        this.validatPer = validatPer;
    }

    public Proveidor getProveidor() {
        return proveidor;
    }

    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }

    public String getFitxerAdjunt() {
        return fitxerAdjunt;
    }

    public void setFitxerAdjunt(String fitxerAdjunt) {
        this.fitxerAdjunt = fitxerAdjunt;
    }

    public Instant getCreat() {
        return creat;
    }

    public void setCreat(Instant creat) {
        this.creat = creat;
    }

    public Instant getActualitzat() {
        return actualitzat;
    }

    public void setActualitzat(Instant actualitzat) {
        this.actualitzat = actualitzat;
    }

// Getter i Setter
    public Usuari getUsuariModificacio() {
        return usuariModificacio;
    }

    public void setUsuariModificacio(Usuari usuariModificacio) {
        this.usuariModificacio = usuariModificacio;
    }

}
