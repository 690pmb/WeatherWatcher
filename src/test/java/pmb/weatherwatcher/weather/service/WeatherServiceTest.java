package pmb.weatherwatcher.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.common.exception.NoContentException;
import pmb.weatherwatcher.common.exception.NotFoundException;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.service.UserService;
import pmb.weatherwatcher.weather.api.client.WeatherApiClient;
import pmb.weatherwatcher.weather.api.model.Astro;
import pmb.weatherwatcher.weather.api.model.Condition;
import pmb.weatherwatcher.weather.api.model.Day;
import pmb.weatherwatcher.weather.api.model.Direction;
import pmb.weatherwatcher.weather.api.model.Forecast;
import pmb.weatherwatcher.weather.api.model.ForecastJsonResponse;
import pmb.weatherwatcher.weather.api.model.Forecastday;
import pmb.weatherwatcher.weather.api.model.Hour;
import pmb.weatherwatcher.weather.api.model.Location;
import pmb.weatherwatcher.weather.api.model.SearchJsonResponse;
import pmb.weatherwatcher.weather.dto.ForecastDayDto;
import pmb.weatherwatcher.weather.dto.ForecastDto;
import pmb.weatherwatcher.weather.dto.HourDto;
import pmb.weatherwatcher.weather.mapper.DayMapperImpl;
import pmb.weatherwatcher.weather.mapper.ForecastDayMapperImpl;
import pmb.weatherwatcher.weather.mapper.ForecastMapperImpl;
import pmb.weatherwatcher.weather.mapper.HourMapperImpl;
import pmb.weatherwatcher.weather.mapper.LocationMapperImpl;

@ServiceTestRunner
@Import({
  WeatherService.class,
  ForecastMapperImpl.class,
  ForecastDayMapperImpl.class,
  HourMapperImpl.class,
  DayMapperImpl.class,
  LocationMapperImpl.class
})
class WeatherServiceTest {

  @MockBean private WeatherApiClient weatherApiClient;
  @MockBean private UserService userService;
  @Autowired private WeatherService weatherService;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(weatherApiClient, userService);
  }

  @Nested
  class FindForecastbyLocation {

    @Test
    void given_location() {
      ForecastJsonResponse response = new ForecastJsonResponse();
      Location location = new Location();
      location.setName("name");
      response.setLocation(location);
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

      when(weatherApiClient.getForecastWeather("lyon", 5, Language.BENGALI))
          .thenReturn(Optional.of(response));

      ForecastDto actual = weatherService.findForecastByLocation("lyon", 5, "bn");

      verify(weatherApiClient).getForecastWeather("lyon", 5, Language.BENGALI);

      List<ForecastDayDto> forecastDay = actual.getForecastDay();
      Astro actualAstro = forecastDay.get(0).getAstro();
      assertAll(
          () -> assertEquals("name", actual.getLocation().getName()),
          () -> assertEquals(1, forecastDay.size()),
          () -> assertEquals(5, actualAstro.getMoonIllumination()),
          () -> assertEquals("phase", actualAstro.getMoonPhase()),
          () -> assertEquals("rise", actualAstro.getMoonrise()),
          () -> assertEquals("moon", actualAstro.getMoonset()),
          () -> assertEquals("set", actualAstro.getSunset()),
          () -> assertEquals("sun", actualAstro.getSunrise()),
          () -> assertEquals("date", forecastDay.get(0).getDate()),
          () ->
              assertThat(response.getForecast().getForecastday().get(0).getDay())
                  .usingRecursiveComparison()
                  .isEqualTo(forecastDay.get(0).getDay()),
          () -> assertEquals(1, forecastDay.get(0).getHour().size()),
          () -> assertHour(forecastDay.get(0).getHour().get(0), response));
    }

    @Test
    void not_found_with_favourite_location() {
      when(weatherApiClient.getForecastWeather("london", null, Language.FRENCH))
          .thenReturn(Optional.empty());
      when(userService.getCurrentUser())
          .thenReturn(new User("test", "test", "london", Language.FRENCH));

      assertThrows(
          NotFoundException.class, () -> weatherService.findForecastByLocation(null, null, null));

      verify(weatherApiClient).getForecastWeather("london", null, Language.FRENCH);
      verify(userService).getCurrentUser();
    }

    @Test
    void no_location_nor_user_favorite_then_no_content() {
      when(userService.getCurrentUser())
          .thenReturn(new User("test", "test", null, Language.FRENCH));

      assertThrows(
          NoContentException.class, () -> weatherService.findForecastByLocation(null, null, null));

      verify(weatherApiClient, never()).getForecastWeather(any(), eq(null), eq(Language.FRENCH));
      verify(userService).getCurrentUser();
    }

    private void assertHour(HourDto hourDto, ForecastJsonResponse response) {
      assertAll(
          () -> assertEquals("time", hourDto.getTime()),
          () -> assertEquals(95D, hourDto.getTempC()),
          () -> assertTrue(hourDto.getIsDay()),
          () ->
              assertThat(hourDto.getCondition())
                  .usingRecursiveComparison()
                  .isEqualTo(
                      response
                          .getForecast()
                          .getForecastday()
                          .get(0)
                          .getHour()
                          .get(0)
                          .getCondition()),
          () -> assertEquals(9D, hourDto.getWindKph()),
          () -> assertEquals(Direction.SE, hourDto.getWindDir()),
          () -> assertEquals(6D, hourDto.getPressureMb()),
          () -> assertEquals(32D, hourDto.getPrecipMm()),
          () -> assertEquals(89, hourDto.getHumidity()),
          () -> assertEquals(5, hourDto.getCloud()),
          () -> assertEquals(89D, hourDto.getFeelsLikeC()),
          () -> assertEquals(5, hourDto.getWillItRain()),
          () -> assertEquals(7, hourDto.getChanceOfRain()),
          () -> assertEquals(96, hourDto.getWillItSnow()),
          () -> assertEquals(12, hourDto.getChanceOfSnow()),
          () -> assertEquals(3D, hourDto.getUv()));
    }

    private Hour buildHour() {
      Hour hour = new Hour();
      hour.setTimeEpoch(96);
      hour.setTime("time");
      hour.setTempC(95D);
      hour.setIsDay(1);
      hour.setCondition(buildCondition());
      hour.setWindKph(9D);
      hour.setWindDegree(62);
      hour.setWindDir(Direction.SE);
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

  @Test
  void searchLocations_ok() {
    SearchJsonResponse response = new SearchJsonResponse();
    response.setId(5);
    response.setName("name");
    response.setRegion("region");
    response.setCountry("country");
    response.setLat(965.3);
    response.setLon(74.2);
    response.setUrl("url");

    when(weatherApiClient.searchLocations("lyon")).thenReturn(List.of(response));

    List<SearchJsonResponse> actual = weatherService.searchLocations("lyon");

    assertAll(
        () -> assertEquals(1, actual.size()),
        () -> assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(response));

    verify(weatherApiClient).searchLocations("lyon");
  }
}
