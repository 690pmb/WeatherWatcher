package pmb.weatherwatcher.weather.dto;

import java.util.List;

public class ForecastDto {

  private LocationDto location;
  private List<ForecastDayDto> forecastDay;

  public LocationDto getLocation() {
    return location;
  }

  public void setLocation(LocationDto location) {
    this.location = location;
  }

  public List<ForecastDayDto> getForecastDay() {
    return forecastDay;
  }

  public void setForecastDay(List<ForecastDayDto> forecastDay) {
    this.forecastDay = forecastDay;
  }
}
