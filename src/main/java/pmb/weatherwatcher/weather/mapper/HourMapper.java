package pmb.weatherwatcher.weather.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import pmb.weatherwatcher.weather.api.model.Direction;
import pmb.weatherwatcher.weather.api.model.Hour;
import pmb.weatherwatcher.weather.dto.HourDto;

@Mapper(imports = BooleanUtils.class)
public interface HourMapper {

  @Mapping(target = "isDay", expression = "java(BooleanUtils.toBooleanObject(hour.getIsDay()))")
  @Mapping(target = "windDir", qualifiedByName = "roundWind")
  HourDto toDto(Hour hour);

  @AfterMapping
  default void mapCondition(@MappingTarget HourDto dto) {
    dto.getCondition().setIcon(StringUtils.substringAfterLast(dto.getCondition().getIcon(), '/'));
  }

  @Named("roundWind")
  default Direction roundWind(Direction windDir) {
    return Direction.values()[
        Long.valueOf(Math.round(Double.valueOf(windDir.ordinal()) / 2)).intValue() * 2 % 16];
  }
}
