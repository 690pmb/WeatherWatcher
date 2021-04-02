package pmb.weatherwatcher.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import pmb.weatherwatcher.dto.alert.AlertDto;
import pmb.weatherwatcher.model.Alert;

/**
 * Maps {@link AlertDto} with {@link Alert}.
 *
 * @see EntityDtoMapper
 */
@Mapper(uses = { MonitoredFieldMapper.class })
public interface AlertMapper
        extends EntityDtoMapper<Alert, AlertDto> {

    @Override
    AlertDto toDto(Alert entity);

    @Override
    @Mapping(target = "user", ignore = true)
    @InheritInverseConfiguration
    Alert toEntity(AlertDto dto);

    @AfterMapping
    default void setAlert(@MappingTarget Alert entity) {
        entity.getMonitoredFields().forEach(field -> field.setAlert(entity));
    }

}
