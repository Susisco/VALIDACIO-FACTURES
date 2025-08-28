package cat.ajterrassa.validaciofactures.dto;

import java.util.List;

public class AutoRelateDTO {
    private List<AlbaraMatchDTO> albaransAutoRelats;

    public AutoRelateDTO() {}

    public AutoRelateDTO(List<AlbaraMatchDTO> albaransAutoRelats) {
        this.albaransAutoRelats = albaransAutoRelats;
    }

    public List<AlbaraMatchDTO> getAlbaransAutoRelats() {
        return albaransAutoRelats;
    }

    public void setAlbaransAutoRelats(List<AlbaraMatchDTO> albaransAutoRelats) {
        this.albaransAutoRelats = albaransAutoRelats;
    }
}
