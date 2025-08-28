// src/main/java/cat/ajterrassa/validaciofactures/dto/SelectionDTO.java
package cat.ajterrassa.validaciofactures.dto;

import lombok.*;// lombok.experimental.SuperBuilder;
import lombok.Getter;// para que funcioni el @Builder
import lombok.Setter;
import java.util.List;
import lombok.Builder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SelectionDTO {
    private List<Long> pressupostIds;
    private List<Long> albaraIds;
    private Long usuariId;


}
