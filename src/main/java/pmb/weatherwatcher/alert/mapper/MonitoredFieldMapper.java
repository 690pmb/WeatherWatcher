package pmb.weatherwatcher.alert.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pmb.weatherwatcher.alert.dto.MonitoredFieldDto;
import pmb.weatherwatcher.alert.model.MonitoredField;
import pmb.weatherwatcher.common.mapper.EntityDtoMapper;

/**
 * Maps {@link MonitoredFieldDto} with {@link MonitoredField}.
 *
 * @see EntityDtoMapper
 */
@Mapper
public interface MonitoredFieldMapper extends EntityDtoMapper<MonitoredField, MonitoredFieldDto> {

  @Override
  @Mapping(target = "alert", ignore = true)
  MonitoredField toEntity(MonitoredFieldDto dto);
}
