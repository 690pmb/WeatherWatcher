package pmb.weatherwatcher.weather.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import pmb.weatherwatcher.weather.dto.HourDto;
import pmb.weatherwatcher.weather.api.model.Hour;

@Mapper(imports = BooleanUtils.class)
public interface HourMapper {

    @Mapping(target = "isDay", expression = "java(BooleanUtils.toBooleanObject(hour.getIsDay()))")
    HourDto toDto(Hour hour);

    @AfterMapping
    default void mapCondition(@MappingTarget HourDto dto) {
        dto.getCondition().setIcon(StringUtils.substringAfterLast(dto.getCondition().getIcon(), '/'));
    }

}
