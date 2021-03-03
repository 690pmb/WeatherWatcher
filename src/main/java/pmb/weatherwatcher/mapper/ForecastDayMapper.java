package pmb.weatherwatcher.mapper;

import org.mapstruct.Mapper;

import pmb.weatherwatcher.dto.weather.ForecastDayDto;
import pmb.weatherwatcher.weatherapi.model.Forecastday;

@Mapper(uses = { HourMapper.class, DayMapper.class })
public interface ForecastDayMapper {

    ForecastDayDto toDto(Forecastday forecastDay);

}
