package cat.ajterrassa.validaciofactures.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AlbaraDetallMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Albara albara;

    @ManyToOne(optional = false)
    private FacturaDetall detall;

    private LocalDateTime dataCreacio = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public Albara getAlbara() {
        return albara;
    }

    public void setAlbara(Albara albara) {
        this.albara = albara;
    }

    public FacturaDetall getDetall() {
        return detall;
    }

    public void setDetall(FacturaDetall detall) {
        this.detall = detall;
    }

    public LocalDateTime getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(LocalDateTime dataCreacio) {
        this.dataCreacio = dataCreacio;
    }
}
