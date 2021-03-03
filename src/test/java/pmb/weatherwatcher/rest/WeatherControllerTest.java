package pmb.weatherwatcher.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import pmb.weatherwatcher.TestUtils;
import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.dto.weather.IpDto;
import pmb.weatherwatcher.exception.NotFoundException;
import pmb.weatherwatcher.security.JwtTokenProvider;
import pmb.weatherwatcher.security.MyUserDetailsService;
import pmb.weatherwatcher.service.WeatherService;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@ActiveProfiles("test")
@Import({ JwtTokenProvider.class })
@WebMvcTest(controllers = WeatherController.class)
@MockBean(MyUserDetailsService.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private WeatherService weatherService;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(weatherService);
    }

    RequestPostProcessor remoteAddr = request -> {
        request.setRemoteAddr("ipv4");
        return request;
    };

    @Nested
    class FindForecastbyLocation {

        @Test
        @WithMockUser
        void ok() throws Exception {
            ForecastDto response = new ForecastDto();
            response.setLocation("test");

            when(weatherService.findForecastbyLocation("lyon", 5, "bn", "ipv4")).thenReturn(response);

            assertEquals("test",
                    objectMapper
                            .readValue(
                                    TestUtils.readResponse
                                            .apply(mockMvc
                                                    .perform(get("/weathers").with(remoteAddr).param("location", "lyon").param("days", "5")
                                                            .param("lang", "bn").contentType(MediaType.APPLICATION_JSON_VALUE))
                                                    .andExpect(status().isOk())),
                                    ForecastDto.class)
                            .getLocation());

            verify(weatherService).findForecastbyLocation("lyon", 5, "bn", "ipv4");
        }

        @Test
        @WithMockUser
        void not_found() throws Exception {
            when(weatherService.findForecastbyLocation(null, null, null, "ipv4")).thenThrow(new NotFoundException("Error"));

            mockMvc.perform(get("/weathers").with(remoteAddr).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound())
                    .andExpect(jsonPath("$").value("Error"));

            verify(weatherService).findForecastbyLocation(null, null, null, "ipv4");
        }

        @Test
        void when_not_logged_then_unauthorized() throws Exception {
            mockMvc.perform(
                    get("/weathers").with(remoteAddr).param("location", "lyon").param("days", "5").contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnauthorized()).andExpect(jsonPath("$").doesNotExist());

            verify(weatherService, never()).findForecastbyLocation("lyon", 5, null, "ipv4");
        }

    }

    @Nested
    class FindLocationByIp {

        @Test
        @WithMockUser
        void ok() throws Exception {
            IpDto response = new IpDto();
            response.setIp("test");

            when(weatherService.findLocationByIp("ipv4")).thenReturn(response);

            assertEquals("test",
                    objectMapper.readValue(TestUtils.readResponse.apply(mockMvc
                            .perform(get("/weathers/ip").with(remoteAddr).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())),
                            IpDto.class).getIp());

            verify(weatherService).findLocationByIp("ipv4");
        }

        @Test
        @WithMockUser
        void not_found() throws Exception {
            when(weatherService.findLocationByIp("ipv4")).thenThrow(NotFoundException.class);

            mockMvc.perform(get("/weathers/ip").with(remoteAddr).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

            verify(weatherService).findLocationByIp("ipv4");
        }

        @Test
        void not_authenticate_then_unauthorized() throws Exception {
            mockMvc.perform(get("/weathers/ip").with(remoteAddr).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isUnauthorized());

            verify(weatherService, never()).findLocationByIp("ipv4");
        }

    }

    @Nested
    class SearchLocations {

        @Test
        void ok() throws Exception {
            SearchJsonResponse response = new SearchJsonResponse();
            response.setId(5);
            response.setName("name");
            response.setRegion("region");
            response.setCountry("country");
            response.setLat(965.3);
            response.setLon(74.2);
            response.setUrl("url");

            when(weatherService.searchLocations("lyon")).thenReturn(List.of(response));

            SearchJsonResponse[] actual = objectMapper.readValue(TestUtils.readResponse
                    .apply(mockMvc.perform(get("/weathers/locations").param("query", "lyon").contentType(MediaType.APPLICATION_JSON_VALUE))
                            .andExpect(status().isOk())),
                    SearchJsonResponse[].class);

            assertAll(() -> assertEquals(1, actual.length), () -> assertThat(actual[0]).usingRecursiveComparison().isEqualTo(response));

            verify(weatherService).searchLocations("lyon");
        }

        @Test
        void missing_query() throws Exception {
            mockMvc.perform(get("/weathers/locations").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());

            verify(weatherService, never()).searchLocations("");
        }

    }

}
