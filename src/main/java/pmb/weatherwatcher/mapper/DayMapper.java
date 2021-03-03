package pmb.weatherwatcher.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import pmb.weatherwatcher.dto.weather.DayDto;
import pmb.weatherwatcher.weatherapi.model.Day;

@Mapper
public interface DayMapper {

    DayDto toDto(Day day);

    @AfterMapping
    default void mapCondition(@MappingTarget DayDto dto) {
        dto.getCondition().setIcon(StringUtils.substringAfterLast(dto.getCondition().getIcon(), '/'));
    }

}
