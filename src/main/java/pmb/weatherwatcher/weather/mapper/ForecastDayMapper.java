package pmb.weatherwatcher.weather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pmb.weatherwatcher.weather.api.model.Forecastday;
import pmb.weatherwatcher.weather.dto.ForecastDayDto;

@Mapper(uses = {HourMapper.class, DayMapper.class})
public interface ForecastDayMapper {

  @Mapping(target = "location", ignore = true)
  ForecastDayDto toDto(Forecastday forecastDay);
}
