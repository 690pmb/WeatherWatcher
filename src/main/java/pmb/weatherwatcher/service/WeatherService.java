package pmb.weatherwatcher.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.exception.NoContentException;
import pmb.weatherwatcher.exception.NotFoundException;
import pmb.weatherwatcher.mapper.ForecastMapper;
import pmb.weatherwatcher.weatherapi.client.WeatherApiClient;
import pmb.weatherwatcher.weatherapi.model.Language;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@Service
public class WeatherService {

    private WeatherApiClient weatherApiClient;
    private UserService userService;
    private ForecastMapper forecastMapper;

    public WeatherService(WeatherApiClient weatherApiClient, UserService userService, ForecastMapper forecastMapper) {
        this.weatherApiClient = weatherApiClient;
        this.userService = userService;
        this.forecastMapper = forecastMapper;
    }

    /**
     * Finds forecast for given location. If not provided uses user's favorite location.
     *
     * @param location city, coordinates (lont/lat)
     * @param days number of days of forecast required
     * @param lang language
     * @return a {@link ForecastDto}
     */
    public ForecastDto findForecastbyLocation(String location, Integer days, String lang) {
        String foundLocation = Optional.ofNullable(location).map(StringUtils::trim).filter(StringUtils::isNotBlank)
                .or(() -> Optional.ofNullable(userService.getCurrentUser().getFavouriteLocation()))
                .orElseThrow(() -> new NoContentException("No location when requesting forecast"));
        return weatherApiClient.getForecastWeather(foundLocation, days, Optional.ofNullable(lang).flatMap(Language::fromCode).orElse(Language.FRENCH))
                .map(forecastMapper::toDto).orElseThrow(() -> new NotFoundException("Could not find forecast for location: '" + location + "'"));
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
