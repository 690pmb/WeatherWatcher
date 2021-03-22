package pmb.weatherwatcher.weatherapi.client.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import pmb.weatherwatcher.weatherapi.client.WeatherApiClient;
import pmb.weatherwatcher.weatherapi.config.WeatherApiProperties;
import pmb.weatherwatcher.weatherapi.exception.WeatherApiClientException;
import pmb.weatherwatcher.weatherapi.model.ForecastJsonResponse;
import pmb.weatherwatcher.weatherapi.model.Language;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@Component
public class WeatherApiClientImpl
        implements WeatherApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherApiClientImpl.class);

    private static final String ERROR_LOG = "Error while accessing url: {}";
    private static final String PARAM_API_KEY = "key";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_DAYS = "days";
    private static final String PARAM_LANGUAGE = "lang";
    private static final String URL_FORECAST = "/forecast.json";
    private static final String URL_SEARCH = "/search.json";

    private RestTemplate restTemplate;
    private WeatherApiProperties weatherApiProperties;

    public WeatherApiClientImpl(RestTemplate restTemplate, WeatherApiProperties weatherApiProperties) {
        this.restTemplate = restTemplate;
        this.weatherApiProperties = weatherApiProperties;
    }

    @Override
    public Optional<ForecastJsonResponse> getForecastWeather(String location, Integer days, Language lang) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(PARAM_QUERY, location);
        if (days != null) {
            params.add(PARAM_DAYS, String.valueOf(days));
        }
        params.add(PARAM_LANGUAGE, lang.getCode());
        return get(URL_FORECAST, params, ForecastJsonResponse.class);
    }

    @Override
    public List<SearchJsonResponse> searchLocations(String query) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(PARAM_QUERY, query);
        return get(URL_SEARCH, params, SearchJsonResponse[].class).map(List::of).orElse(Collections.emptyList());
    }

    private <T> Optional<T> get(String path, MultiValueMap<String, String> params, Class<T> responseType) {
        String url = buildUrl(path, params);
        LOGGER.debug("Requesting url: {}", url);
        try {
            return Optional.ofNullable(restTemplate.exchange(url, HttpMethod.GET, null, responseType).getBody());
        } catch (RestClientException e) {
            LOGGER.error(ERROR_LOG, url);
            throw new WeatherApiClientException(e.getMessage(), e);
        }
    }

    private String buildUrl(String path, MultiValueMap<String, String> params) {
        return UriComponentsBuilder.fromUriString(weatherApiProperties.getBaseUrl()).path(path)
                .queryParam(PARAM_API_KEY, weatherApiProperties.getApiKey()).queryParams(params).build(false).toUriString();
    }

}
