package pmb.weatherwatcher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.weatherapi.model.ForecastJsonResponse;

@Mapper(uses = { ForecastDayMapper.class, LocationMapper.class })
public interface ForecastMapper {

    @Mapping(target = "forecastDay", source = "forecast.forecastday")
    ForecastDto toDto(ForecastJsonResponse forecast);

}
