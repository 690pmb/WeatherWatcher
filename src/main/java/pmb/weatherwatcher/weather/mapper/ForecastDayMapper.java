package pmb.weatherwatcher.weather.mapper;

import org.mapstruct.Mapper;
import pmb.weatherwatcher.weather.api.model.Forecastday;
import pmb.weatherwatcher.weather.dto.ForecastDayDto;

@Mapper(uses = {HourMapper.class, DayMapper.class})
public interface ForecastDayMapper {

  ForecastDayDto toDto(Forecastday forecastDay);
}
