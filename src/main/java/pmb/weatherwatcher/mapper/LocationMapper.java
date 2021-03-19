package pmb.weatherwatcher.mapper;

import org.mapstruct.Mapper;

import pmb.weatherwatcher.dto.weather.LocationDto;
import pmb.weatherwatcher.weatherapi.model.Location;

@Mapper
public interface LocationMapper {

    LocationDto toDto(Location location);

}
