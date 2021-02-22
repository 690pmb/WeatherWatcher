package pmb.weatherwatcher.mapper;

import org.mapstruct.Mapper;

import pmb.weatherwatcher.dto.weather.HourDto;
import pmb.weatherwatcher.weatherapi.model.Hour;

@Mapper
public interface HourMapper {

    HourDto toDto(Hour hour);

}
