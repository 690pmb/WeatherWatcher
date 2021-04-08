package pmb.weatherwatcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pmb.weatherwatcher.dto.alert.MonitoredFieldDto;
import pmb.weatherwatcher.model.MonitoredField;

/**
 * Maps {@link MonitoredFieldDto} with {@link MonitoredField}.
 *
 * @see EntityDtoMapper
 */
@Mapper
public interface MonitoredFieldMapper
        extends EntityDtoMapper<MonitoredField, MonitoredFieldDto> {

    @Override
    @Mapping(target = "alert", ignore = true)
    MonitoredField toEntity(MonitoredFieldDto dto);

}
