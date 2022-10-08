package pmb.weatherwatcher.alert.mapper;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
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
  @Mapping(target = "triggerHour", qualifiedByName = "localTimeToOffsetTime")
  @Mapping(target = "monitoredHours", qualifiedByName = "localTimeToOffsetTime")
  AlertDto toDto(Alert entity);

  @Override
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "triggerHour", qualifiedByName = "offsetTimeToLocalTime")
  @Mapping(target = "monitoredHours", qualifiedByName = "offsetTimeToLocalTime")
  @InheritInverseConfiguration
  Alert toEntity(AlertDto dto);

  @AfterMapping
  default void setAlert(@MappingTarget Alert entity) {
    entity.getMonitoredFields().forEach(field -> field.setAlert(entity));
  }

  @Named("offsetTimeToLocalTime")
  default LocalTime offsetTimeToLocalTime(OffsetTime triggerHour) {
    return Optional.ofNullable(triggerHour)
        .map(t -> t.withOffsetSameInstant(ZoneOffset.UTC).toLocalTime())
        .orElse(null);
  }

  @Named("localTimeToOffsetTime")
  default OffsetTime localTimeToOffsetTime(LocalTime triggerHour) {
    return Optional.ofNullable(triggerHour).map(t -> OffsetTime.of(t, ZoneOffset.UTC)).orElse(null);
  }
}
