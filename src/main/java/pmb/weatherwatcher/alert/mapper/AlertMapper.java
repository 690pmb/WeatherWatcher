package pmb.weatherwatcher.alert.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.model.Alert;
import pmb.weatherwatcher.common.mapper.EntityDtoMapper;

/**
 * Maps {@link AlertDto} with {@link Alert}.
 *
 * @see EntityDtoMapper
 */
@Mapper(uses = {MonitoredFieldMapper.class})
public interface AlertMapper extends EntityDtoMapper<Alert, AlertDto> {

  @Override
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "timezone", ignore = true)
  AlertDto toDto(Alert entity);

  @Mapping(target = "timezone", source = "entity.user.timezone")
  @Mapping(target = "user", source = "entity.user.login")
  AlertDto toDtoWithUserData(Alert entity);

  @Override
  @Mapping(target = "user", ignore = true)
  Alert toEntity(AlertDto dto);

  @AfterMapping
  default void setAlert(@MappingTarget Alert entity) {
    entity.getMonitoredFields().forEach(field -> field.setAlert(entity));
  }
}
