// DocumentUbicacio.java
package cat.ajterrassa.validaciofactures.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DocumentUbicacio extends DocumentBase {

    @ManyToOne
    @JoinColumn(name = "edifici_id")
    private Edifici edifici;

    @ManyToOne
    @JoinColumn(name = "ots_id")
    private OTS ots;

    // Getter y Setter para edifici
    public Edifici getEdifici() {
        return edifici;
    }

    public void setEdifici(Edifici edifici) {
        this.edifici = edifici;
    }

    // Getter y Setter para ots
    public OTS getOts() {
        return ots;
    }

    public void setOts(OTS ots) {
        this.ots = ots;
    }
}