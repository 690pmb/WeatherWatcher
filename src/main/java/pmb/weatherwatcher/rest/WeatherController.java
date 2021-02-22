package pmb.weatherwatcher.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.dto.weather.IpDto;
import pmb.weatherwatcher.exception.NotFoundException;
import pmb.weatherwatcher.mapper.ForecastMapper;
import pmb.weatherwatcher.mapper.IpMapper;
import pmb.weatherwatcher.weatherapi.client.WeatherApiClient;
import pmb.weatherwatcher.weatherapi.model.Language;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@RestController
@RequestMapping(path = "/weathers")
public class WeatherController {

    private WeatherApiClient weatherApiClient;
    private ForecastMapper forecastMapper;
    private IpMapper ipMapper;

    public WeatherController(WeatherApiClient weatherApiClient, ForecastMapper forecastMapper, IpMapper ipMapper) {
        this.weatherApiClient = weatherApiClient;
        this.forecastMapper = forecastMapper;
        this.ipMapper = ipMapper;
    }

    @GetMapping("/{location}")
    public List<ForecastDto> findForecastbyLocation(@PathVariable String location, @RequestParam(required = false) Integer days,
            @RequestParam(required = false) String lang) {
        return weatherApiClient.getForecastWeather(location, days, Optional.ofNullable(lang).flatMap(Language::fromCode).orElse(Language.FRENCH))
                .map(response -> response.getForecast().getForecastday())
                .orElseThrow(() -> new NotFoundException("Could not find forecast for location: " + location)).stream().map(forecastMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/ip/{ip}")
    public IpDto findLocationByIp(@PathVariable String ip) {
        return weatherApiClient.getIpLookup(ip).map(ipMapper::toDto).orElseThrow(() -> new NotFoundException("Could not find ip: " + ip));
    }

    @GetMapping("/locations")
    public List<SearchJsonResponse> searchLocations(@RequestParam String query) {
        return weatherApiClient.searchLocations(query);
    }

}
