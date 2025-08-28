// src/main/java/cat/ajterrassa/validaciofactures/dto/SuggerimentsDTO.java
package cat.ajterrassa.validaciofactures.dto;

import cat.ajterrassa.validaciofactures.model.Albara;
import cat.ajterrassa.validaciofactures.model.Pressupost;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.util.List;
import lombok.Data;


@Data
@Builder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SuggerimentsDTO {
    private List<Pressupost> pressupostosCandidats;
    private List<Albara> albaransCandidats;
}
