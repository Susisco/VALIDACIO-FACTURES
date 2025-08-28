package cat.ajterrassa.validaciofactures.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;   

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Pressupost extends DocumentUbicacio {

    // hereta: tipus,data,importTotal,estat,creador,validatPer,proveidor,fitxerAdjunt,creat,actualitzat
    // i edifici, ots de DocumentUbicacio

    @ManyToOne @JoinColumn(name = "factura_id")
    private Factura factura;

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    
    

}
