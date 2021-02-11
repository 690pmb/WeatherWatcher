package pmb.weatherwatcher.weatherapi.client.impl;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import pmb.weatherwatcher.weatherapi.config.WeatherApiProperties;
import pmb.weatherwatcher.weatherapi.exception.WeatherApiClientException;
import pmb.weatherwatcher.weatherapi.model.ForecastJsonResponse;
import pmb.weatherwatcher.weatherapi.model.IpJsonResponse;
import pmb.weatherwatcher.weatherapi.model.Language;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@RestClientTest(components = { WeatherApiClientImpl.class, WeatherApiProperties.class }, properties = "weather-api.api-key=api_key_test")
@AutoConfigureWebClient(registerRestTemplate = true)
class WeatherApiClientImplTest {

    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WeatherApiClientImpl weatherApiClientImpl;

    @AfterEach
    void tearDown() {
        server.verify();
        server.reset();
    }

    @Nested
    class getForecastWeather {

        @Test
        void success() throws IOException {
            String forecast = readResponseFile("forecast.json");

            server.expect(requestTo("https://api.weatherapi.com/v1/forecast.json?key=api_key_test&q=lyon&days=2&lang=fi"))
                    .andRespond(withSuccess(forecast, MediaType.APPLICATION_JSON));

            Optional<ForecastJsonResponse> result = weatherApiClientImpl.getForecastWeather("lyon", 2, Language.FINNISH);

            assertAll(() -> assertTrue(result.isPresent()), () -> assertJsonEquals(forecast, objectMapper.writeValueAsString(result.get())));
        }

        @Test
        void error() throws IOException {
            server.expect(requestTo("https://api.weatherapi.com/v1/forecast.json?key=api_key_test&q=lyon&lang=it")).andRespond(withBadRequest());

            assertThrows(WeatherApiClientException.class, () -> weatherApiClientImpl.getForecastWeather("lyon", null, Language.ITALIAN));
        }

    }

    @Nested
    class getIpLookup {

        @Test
        void success() throws IOException {
            String ip = readResponseFile("ip.json");

            server.expect(requestTo("https://api.weatherapi.com/v1/ip.json?key=api_key_test&q=90.25.12.280"))
                    .andRespond(withSuccess(ip, MediaType.APPLICATION_JSON));

            Optional<IpJsonResponse> result = weatherApiClientImpl.getIpLookup("90.25.12.280");

            assertAll(() -> assertTrue(result.isPresent()), () -> assertJsonEquals(ip, objectMapper.writeValueAsString(result.get())));
        }

        @Test
        void error() throws IOException {
            server.expect(requestTo("https://api.weatherapi.com/v1/ip.json?key=api_key_test&q=90.25.12.180")).andRespond(withUnauthorizedRequest());

            assertThrows(WeatherApiClientException.class, () -> weatherApiClientImpl.getIpLookup("90.25.12.180"));
        }

    }

    @Nested
    class searchLocations {

        @Test
        void success() throws IOException {
            String search = readResponseFile("search.json");

            server.expect(requestTo("https://api.weatherapi.com/v1/search.json?key=api_key_test&q=lyon"))
                    .andRespond(withSuccess(search, MediaType.APPLICATION_JSON));

            List<SearchJsonResponse> result = weatherApiClientImpl.searchLocations("lyon");

            assertAll(() -> assertFalse(CollectionUtils.isEmpty(result)), () -> assertJsonEquals(search, objectMapper.writeValueAsString(result)));
        }

        @Test
        void empty() throws IOException {
            server.expect(requestTo("https://api.weatherapi.com/v1/search.json?key=api_key_test&q=lyon"))
                    .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

            List<SearchJsonResponse> result = weatherApiClientImpl.searchLocations("lyon");

            assertTrue(CollectionUtils.isEmpty(result));
        }

        @Test
        void error() throws IOException {
            server.expect(requestTo("https://api.weatherapi.com/v1/search.json?key=api_key_test&q=test")).andRespond(withServerError());

            assertThrows(WeatherApiClientException.class, () -> weatherApiClientImpl.searchLocations("test"));
        }

    }

    private String readResponseFile(String fileName) throws IOException, FileNotFoundException {
        return Files.readString(ResourceUtils.getFile("classpath:weatherapi/" + fileName).toPath());
    }

}
