package pmb.weatherwatcher.weather.mapper;

import org.mapstruct.Mapper;

import pmb.weatherwatcher.weather.dto.LocationDto;
import pmb.weatherwatcher.weather.api.model.Location;

@Mapper
public interface LocationMapper {

    LocationDto toDto(Location location);

}
