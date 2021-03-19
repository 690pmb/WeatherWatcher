package pmb.weatherwatcher.weatherapi.client;

import java.util.List;
import java.util.Optional;

import pmb.weatherwatcher.weatherapi.model.ForecastJsonResponse;
import pmb.weatherwatcher.weatherapi.model.Language;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

public interface WeatherApiClient {

    /**
     * Gets weather forecast for specific location, days and language.
     *
     * @param location can be lat/long (decimal degree) or city name
     * @param days number of days of forecast required (between 1 (default) and 10)
     * @param lang Returns 'condition:text' field in API in the desired language
     * @return an Optional {@link ForecastJsonResponse}
     */
    Optional<ForecastJsonResponse> getForecastWeather(String location, Integer days, Language lang);

    /**
     * Searches matching cities and towns.
     *
     * @param query location to find
     * @return a list of {@link SearchJsonResponse}
     */
    List<SearchJsonResponse> searchLocations(String query);

}
