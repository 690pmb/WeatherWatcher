package pmb.weatherwatcher.mapper;

import org.mapstruct.Mapper;

import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.weatherapi.model.Forecastday;

@Mapper(uses = HourMapper.class)
public interface ForecastMapper {

    ForecastDto toDto(Forecastday forecastday);

}
