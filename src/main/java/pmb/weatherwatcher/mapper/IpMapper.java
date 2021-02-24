package pmb.weatherwatcher.mapper;

import org.mapstruct.Mapper;

import pmb.weatherwatcher.dto.weather.IpDto;
import pmb.weatherwatcher.weatherapi.model.IpJsonResponse;

@Mapper
public interface IpMapper {

    IpDto toDto(IpJsonResponse ipJsonResponse);

}
