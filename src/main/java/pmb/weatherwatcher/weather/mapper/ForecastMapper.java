package pmb.weatherwatcher.weather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pmb.weatherwatcher.weather.api.model.ForecastJsonResponse;
import pmb.weatherwatcher.weather.dto.ForecastDto;

@Mapper(uses = {ForecastDayMapper.class, LocationMapper.class})
public interface ForecastMapper {

  @Mapping(target = "forecastDay", source = "forecast.forecastday")
  ForecastDto toDto(ForecastJsonResponse forecast);
}
