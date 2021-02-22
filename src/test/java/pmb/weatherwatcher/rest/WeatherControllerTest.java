package pmb.weatherwatcher.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import pmb.weatherwatcher.TestUtils;
import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.dto.weather.HourDto;
import pmb.weatherwatcher.dto.weather.IpDto;
import pmb.weatherwatcher.mapper.ForecastMapperImpl;
import pmb.weatherwatcher.mapper.HourMapperImpl;
import pmb.weatherwatcher.mapper.IpMapperImpl;
import pmb.weatherwatcher.security.JwtTokenProvider;
import pmb.weatherwatcher.security.MyUserDetailsService;
import pmb.weatherwatcher.weatherapi.client.WeatherApiClient;
import pmb.weatherwatcher.weatherapi.model.Astro;
import pmb.weatherwatcher.weatherapi.model.Condition;
import pmb.weatherwatcher.weatherapi.model.Day;
import pmb.weatherwatcher.weatherapi.model.Forecast;
import pmb.weatherwatcher.weatherapi.model.ForecastJsonResponse;
import pmb.weatherwatcher.weatherapi.model.Forecastday;
import pmb.weatherwatcher.weatherapi.model.Hour;
import pmb.weatherwatcher.weatherapi.model.IpJsonResponse;
import pmb.weatherwatcher.weatherapi.model.Language;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@ActiveProfiles("test")
@Import({ JwtTokenProvider.class, ForecastMapperImpl.class, IpMapperImpl.class, HourMapperImpl.class })
@WebMvcTest(controllers = WeatherController.class)
@MockBean(MyUserDetailsService.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private WeatherApiClient weatherApiClient;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(weatherApiClient);
    }

    @Nested
    class FindForecastbyLocation {

        @Test
        @WithMockUser
        void ok() throws Exception {
            ForecastJsonResponse response = new ForecastJsonResponse();
            Astro astro = buildAstro();
            Forecastday forecastday = new Forecastday();
            forecastday.setAstro(astro);
            Forecast forecast = new Forecast();
            forecast.setForecastday(List.of(forecastday));
            response.setForecast(forecast);
            Day day = buildDay();
            forecastday.setDay(day);
            Hour hour = buildHour();
            forecastday.setHour(List.of(hour));
            forecastday.setDate("date");

            when(weatherApiClient.getForecastWeather("lyon", 5, Language.BENGALI)).thenReturn(Optional.of(response));

            ForecastDto[] actual = objectMapper.readValue(TestUtils.readResponse.apply(mockMvc
                    .perform(get("/weathers/{location}", "lyon").param("days", "5").param("lang", "bn").contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())), ForecastDto[].class);

            verify(weatherApiClient).getForecastWeather("lyon", 5, Language.BENGALI);

            Astro actualAstro = actual[0].getAstro();
            assertAll(() -> assertEquals(1, actual.length), () -> assertEquals(5, actualAstro.getMoonIllumination()),
                    () -> assertEquals("phase", actualAstro.getMoonPhase()), () -> assertEquals("rise", actualAstro.getMoonrise()),
                    () -> assertEquals("moon", actualAstro.getMoonset()), () -> assertEquals("set", actualAstro.getSunset()),
                    () -> assertEquals("sun", actualAstro.getSunrise()), () -> assertEquals("date", actual[0].getDate()),
                    () -> assertThat(response.getForecast().getForecastday().get(0).getDay()).usingRecursiveComparison()
                            .isEqualTo(actual[0].getDay()),
                    () -> assertEquals(1, actual[0].getHour().size()), () -> assertHour(actual[0].getHour().get(0), response));
        }

        @Test
        @WithMockUser
        void not_found() throws Exception {
            when(weatherApiClient.getForecastWeather("lyon", null, Language.FRENCH)).thenReturn(Optional.empty());

            mockMvc.perform(get("/weathers/{location}", "lyon").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound())
                    .andExpect(jsonPath("$").value("Could not find forecast for location: lyon"));

            verify(weatherApiClient).getForecastWeather("lyon", null, Language.FRENCH);
        }

        @Test
        void when_not_logged_then_unauthorized() throws Exception {
            mockMvc.perform(get("/weathers/{location}", "lyon").param("days", "5").contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnauthorized()).andExpect(jsonPath("$").doesNotExist());

            verify(weatherApiClient, never()).getForecastWeather("lyon", 5, Language.FRENCH);
        }

        private void assertHour(HourDto hourDto, ForecastJsonResponse response) {
            assertAll(() -> assertEquals("time", hourDto.getTime()), () -> assertEquals(95D, hourDto.getTempC()),
                    () -> assertEquals(5, hourDto.getIsDay()),
                    () -> assertThat(hourDto.getCondition()).usingRecursiveComparison()
                            .isEqualTo(response.getForecast().getForecastday().get(0).getHour().get(0).getCondition()),
                    () -> assertEquals(9D, hourDto.getWindKph()), () -> assertEquals("dir", hourDto.getWindDir()),
                    () -> assertEquals(6D, hourDto.getPressureMb()), () -> assertEquals(32D, hourDto.getPrecipMm()),
                    () -> assertEquals(89, hourDto.getHumidity()), () -> assertEquals(5, hourDto.getCloud()),
                    () -> assertEquals(89D, hourDto.getFeelsLikeC()), () -> assertEquals(5, hourDto.getWillItRain()),
                    () -> assertEquals(7, hourDto.getChanceOfRain()), () -> assertEquals(96, hourDto.getWillItSnow()),
                    () -> assertEquals(12, hourDto.getChanceOfSnow()), () -> assertEquals(3D, hourDto.getUv()));
        }

        private Hour buildHour() {
            Hour hour = new Hour();
            hour.setTimeEpoch(96);
            hour.setTime("time");
            hour.setTempC(95D);
            hour.setIsDay(5);
            hour.setCondition(buildCondition());
            hour.setWindKph(9D);
            hour.setWindDegree(62);
            hour.setWindDir("dir");
            hour.setPressureMb(6D);
            hour.setPrecipMm(32D);
            hour.setHumidity(89);
            hour.setCloud(5);
            hour.setFeelsLikeC(89D);
            hour.setWindChillC(78D);
            hour.setHeatIndexC(36D);
            hour.setDewPointC(99D);
            hour.setWillItRain(5);
            hour.setChanceOfRain(7);
            hour.setWillItSnow(96);
            hour.setChanceOfSnow(12);
            hour.setVisKm(14D);
            hour.setUv(3D);
            hour.setGustKph(45D);
            return hour;
        }

        private Day buildDay() {
            Day day = new Day();
            day.setMaxTempC(5.3);
            day.setMinTempC(9.6);
            day.setAvgTempC(83.9);
            day.setMaxWindKph(89D);
            day.setTotalPrecipMm(65.6);
            day.setAvgVisKm(95D);
            day.setAvgHumidity(5D);
            Condition condition = buildCondition();
            day.setCondition(condition);
            day.setUv(2D);
            day.setDailyWillItRain(4);
            day.setDailyChanceOfRain(45);
            day.setDailyWillItSnow(45);
            day.setDailyChanceOfSnow(6);
            return day;
        }

        private Condition buildCondition() {
            Condition condition = new Condition();
            condition.setCode(5);
            condition.setIcon("icon");
            condition.setText("text");
            return condition;
        }

        private Astro buildAstro() {
            Astro astro = new Astro();
            astro.setMoonIllumination(5);
            astro.setMoonPhase("phase");
            astro.setMoonrise("rise");
            astro.setMoonset("moon");
            astro.setSunrise("sun");
            astro.setSunset("set");
            return astro;
        }

    }

    @Nested
    class FindLocationByIp {

        @Test
        @WithMockUser
        void ok() throws Exception {
            IpJsonResponse response = new IpJsonResponse();
            response.setCity("lyon");
            response.setContinentCode("code");
            response.setContinentName("name");
            response.setCountryCode("country");
            response.setCountryName("fr");
            response.setIp("ip");
            response.setType("type");
            response.setIsEu(true);
            response.setGeoNameId(96);
            response.setRegion("rhone");
            response.setLat(5.36);
            response.setLon(96.14);
            response.setTzId("zone");
            response.setLocaltimeEpoch(5952);
            response.setLocaltime("local");

            when(weatherApiClient.getIpLookup("ipv4")).thenReturn(Optional.of(response));

            IpDto actual = objectMapper.readValue(TestUtils.readResponse.apply(
                    mockMvc.perform(get("/weathers/ip/{ip}", "ipv4").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())),
                    IpDto.class);

            assertAll(() -> assertEquals("ip", actual.getIp()), () -> assertEquals("type", actual.getType()),
                    () -> assertEquals("code", actual.getContinentCode()), () -> assertEquals("name", actual.getContinentName()),
                    () -> assertEquals("country", actual.getCountryCode()), () -> assertEquals("fr", actual.getCountryName()),
                    () -> assertTrue(actual.getIsEu()), () -> assertEquals("lyon", actual.getCity()), () -> assertEquals("rhone", actual.getRegion()),
                    () -> assertEquals(5.36, actual.getLat()), () -> assertEquals(96.14, actual.getLon()),
                    () -> assertEquals("zone", actual.getTzId()));

            verify(weatherApiClient).getIpLookup("ipv4");
        }

        @Test
        @WithMockUser
        void not_found() throws Exception {
            when(weatherApiClient.getIpLookup("ipv4")).thenReturn(Optional.empty());

            mockMvc.perform(get("/weathers/ip/{ip}", "ipv4").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

            verify(weatherApiClient).getIpLookup("ipv4");
        }

        @Test
        void not_authenticate_then_unauthorized() throws Exception {
            mockMvc.perform(get("/weathers/ip/{ip}", "ipv4").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isUnauthorized());

            verify(weatherApiClient, never()).getIpLookup("ipv4");
        }

    }

    @Nested
    class SearchLocations {

        @Test
        @WithMockUser
        void ok() throws Exception {
            SearchJsonResponse response = new SearchJsonResponse();
            response.setId(5);
            response.setName("name");
            response.setRegion("region");
            response.setCountry("country");
            response.setLat(965.3);
            response.setLon(74.2);
            response.setUrl("url");

            when(weatherApiClient.searchLocations("lyon")).thenReturn(List.of(response));

            SearchJsonResponse[] actual = objectMapper.readValue(TestUtils.readResponse
                    .apply(mockMvc.perform(get("/weathers/locations").param("query", "lyon").contentType(MediaType.APPLICATION_JSON_VALUE))
                            .andExpect(status().isOk())),
                    SearchJsonResponse[].class);

            assertAll(() -> assertEquals(1, actual.length), () -> assertThat(actual[0]).usingRecursiveComparison().isEqualTo(response));

            verify(weatherApiClient).searchLocations("lyon");
        }

        @Test
        @WithMockUser
        void missing_query() throws Exception {
            mockMvc.perform(get("/weathers/locations").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());

            verify(weatherApiClient, never()).searchLocations("");
        }

        @Test
        void not_authenticate_then_unauthorized() throws Exception {
            mockMvc.perform(get("/weathers/locations").param("query", "lyon").contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnauthorized());

            verify(weatherApiClient, never()).searchLocations("lyon");
        }

    }

}
