package pmb.weatherwatcher.weather;

import java.util.List;
import pmb.weatherwatcher.weather.dto.ForecastDayDto;
import pmb.weatherwatcher.weather.dto.ForecastDto;
import pmb.weatherwatcher.weather.dto.HourDto;
import pmb.weatherwatcher.weather.dto.LocationDto;

public final class WeatherUtils {
  private WeatherUtils() {}

  public static ForecastDto buildForecastDto(String location, List<ForecastDayDto> days) {
    ForecastDto forecast = new ForecastDto();
    LocationDto locationDto = new LocationDto();
    locationDto.setName(location);
    forecast.setLocation(locationDto);
    forecast.setForecastDay(days);
    return forecast;
  }

  public static ForecastDayDto buildForecastDayDto(
      String date, String location, List<HourDto> hours) {
    ForecastDayDto day = new ForecastDayDto();
    day.setLocation(location);
    day.setDate(date);
    day.setHour(hours);
    return day;
  }

  public static HourDto builHourDto(
      String time,
      Double tempC,
      Double windKph,
      String windDir,
      Double pressureMb,
      Double precipMm,
      Integer humidity,
      Integer cloud,
      Double feelsLikeC,
      Integer willItRain,
      Integer chanceOfRain,
      Integer willItSnow,
      Integer chanceOfSnow,
      Double uv) {
    HourDto hour = new HourDto();
    hour.setTime(time);
    hour.setTempC(tempC);
    hour.setWindKph(windKph);
    hour.setWindDir(windDir);
    hour.setPressureMb(pressureMb);
    hour.setPrecipMm(precipMm);
    hour.setHumidity(humidity);
    hour.setCloud(cloud);
    hour.setFeelsLikeC(feelsLikeC);
    hour.setWillItRain(willItRain);
    hour.setChanceOfRain(chanceOfRain);
    hour.setWillItSnow(willItSnow);
    hour.setChanceOfSnow(chanceOfSnow);
    hour.setUv(uv);
    return hour;
  }
}
