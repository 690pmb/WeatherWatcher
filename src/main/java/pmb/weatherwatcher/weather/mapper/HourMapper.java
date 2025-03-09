package pmb.weatherwatcher.weather.mapper;

import java.util.Optional;
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
  @Mapping(
      target = "zonedDateTime",
      expression =
          "java(java.time.ZonedDateTime.ofInstant(java.time.Instant.ofEpochSecond(hour.getTimeEpoch()), java.time.ZoneId.of(\"Z\")))")
  HourDto toDto(Hour hour);

  @AfterMapping
  default void mapCondition(@MappingTarget HourDto dto) {
    Optional.ofNullable(dto.getCondition())
        .ifPresent(
            condition ->
                condition.setIcon(StringUtils.substringAfterLast(condition.getIcon(), '/')));
  }

  @Named("roundWind")
  default Direction roundWind(Direction windDir) {
    return Optional.ofNullable(windDir)
        .map(
            dir ->
                Direction.values()[
                    Long.valueOf(Math.round(Double.valueOf(dir.ordinal()) / 2)).intValue()
                        * 2
                        % 16])
        .orElse(null);
  }
}
