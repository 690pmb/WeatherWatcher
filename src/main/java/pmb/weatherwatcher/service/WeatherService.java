package pmb.weatherwatcher.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.dto.weather.IpDto;
import pmb.weatherwatcher.exception.NotFoundException;
import pmb.weatherwatcher.mapper.ForecastMapper;
import pmb.weatherwatcher.mapper.IpMapper;
import pmb.weatherwatcher.weatherapi.client.WeatherApiClient;
import pmb.weatherwatcher.weatherapi.model.Language;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@Service
public class WeatherService {

    private WeatherApiClient weatherApiClient;
    private UserService userService;
    private ForecastMapper forecastMapper;
    private IpMapper ipMapper;

    public WeatherService(WeatherApiClient weatherApiClient, UserService userService, ForecastMapper forecastMapper, IpMapper ipMapper) {
        this.weatherApiClient = weatherApiClient;
        this.userService = userService;
        this.forecastMapper = forecastMapper;
        this.ipMapper = ipMapper;
    }

    /**
     * Finds forecast for given location. If not provided uses user's favorite location. If not filled, using client's IP address.
     *
     * @param location city, coordinates (lont/lat)
     * @param days number of days of forecast required
     * @param lang language
     * @param ip client IP address
     * @return list of {@link ForecastDto}
     */
    public List<ForecastDto> findForecastbyLocation(String location, Integer days, String lang, String ip) {
        return weatherApiClient
                .getForecastWeather(
                        Optional.ofNullable(location).or(() -> Optional.ofNullable(userService.getCurrentUser().getFavouriteLocation())).orElse(ip),
                        days, Optional.ofNullable(lang).flatMap(Language::fromCode).orElse(Language.FRENCH))
                .map(response -> response.getForecast().getForecastday())
                .orElseThrow(() -> new NotFoundException("Could not find forecast for location: " + location)).stream().map(forecastMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds location by IP address.
     *
     * @param ip given ip
     * @return {@link IpDto}
     */
    public IpDto findLocationByIp(String ip) {
        return weatherApiClient.getIpLookup(ip).map(ipMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Could not find location for ip: " + ip));
    }

    /**
     * Searches location.
     *
     * @param query query term
     * @return a list of suggested {@link SearchJsonResponse}
     */
    public List<SearchJsonResponse> searchLocations(String query) {
        return weatherApiClient.searchLocations(query);
    }

}
