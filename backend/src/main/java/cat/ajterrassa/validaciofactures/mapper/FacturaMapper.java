package cat.ajterrassa.validaciofactures.mapper;

import cat.ajterrassa.validaciofactures.dto.DetallDto;
import cat.ajterrassa.validaciofactures.dto.FacturaDto;
import cat.ajterrassa.validaciofactures.model.Factura;
import cat.ajterrassa.validaciofactures.model.FacturaDetall;

import java.util.stream.Collectors;

public class FacturaMapper {

    public static FacturaDto toDto(Factura entity) {
        FacturaDto dto = new FacturaDto();
        dto.setId(entity.getId());
        dto.setTipus(entity.getTipus());
        dto.setReferenciaDocument(entity.getReferenciaDocument());
        dto.setData(entity.getData());
        dto.setImportTotal(entity.getImportTotal()); // Assigna directament si el tipus al DTO és BigDecimal
        dto.setEstat(entity.getEstat().name());
        dto.setFitxerAdjunt(entity.getFitxerAdjunt());
        dto.setCreat(entity.getCreat());
        dto.setActualitzat(entity.getActualitzat());

        // Detalls
        dto.setDetalls(entity.getDetalls().stream().map(d -> {
            DetallDto dd = new DetallDto();
            dd.setId(d.getId());
            dd.setReferenciaDocumentDetall(d.getReferenciaDocumentDetall());
            dd.setImportTotalDetall(d.getImportTotalDetall()); // JA ÉS BigDecimal, no cal fer .doubleValue()
            dd.setFacturaId(entity.getId());
            return dd;
        }).collect(Collectors.toList()));

        return dto;
    }
}
