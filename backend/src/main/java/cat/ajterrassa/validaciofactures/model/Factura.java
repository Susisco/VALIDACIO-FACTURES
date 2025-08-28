package cat.ajterrassa.validaciofactures.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura extends DocumentBase {

    // Factura.java
    @OneToMany(mappedBy = "factura", // ← Nom de l'atribut a la classe “FacturaDetall”
            cascade = CascadeType.ALL, // ← Propagació de les operacions (persist, merge, remove, refresh, detach)
            orphanRemoval = true) // ← Eliminar detalls orfes (sense factura associada)
    @JsonManagedReference // ← Marca el costat “pare” per evitar la recursivitat
    private List<FacturaDetall> detalls = new ArrayList<>();

    // ja hereta: proveidor, creador, validatPer, tipus, data, importTotal, estat,
    // fitxerAdjunt, timestamps

    public void setDetalls(List<FacturaDetall> detalls) {
        // esborrem els vells i afegim els nous amb la relació fixada
        this.detalls.clear();
        if (detalls != null) {
            detalls.forEach(this::addDetall);
        }
    }

    public void addDetall(FacturaDetall det) {
        det.setFactura(this);
        this.detalls.add(det);
    }


    public List<FacturaDetall> getDetalls() {
        return detalls;
    }

    private Proveidor proveidor;

    public Proveidor getProveidor() {
        return proveidor;
    }

    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }



    
    // Getters and Setters for inherited fields (if needed)
    // These are already provided by Lombok annotations (@Getter and @Setter)
    // No additional code is required here unless you need custom logic.


}
