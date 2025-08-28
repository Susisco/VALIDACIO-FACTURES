package cat.ajterrassa.validaciofactures.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cat.ajterrassa.validaciofactures.model.Factura;
import cat.ajterrassa.validaciofactures.dto.FacturaDto;

@Mapper
public interface FacturaMapper {
    FacturaMapper INSTANCE = Mappers.getMapper(FacturaMapper.class);

    FacturaDto toDto(Factura factura);
    Factura toEntity(FacturaDto dto);

    
}