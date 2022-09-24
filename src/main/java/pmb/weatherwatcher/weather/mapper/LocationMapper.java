package pmb.weatherwatcher.weather.mapper;

import org.mapstruct.Mapper;
import pmb.weatherwatcher.weather.api.model.Location;
import pmb.weatherwatcher.weather.dto.LocationDto;

@Mapper
public interface LocationMapper {

  LocationDto toDto(Location location);
}
