package pmb.weatherwatcher.dto.weather;

import java.util.List;

public class ForecastDto {

    private String location;
    private List<ForecastDayDto> forecastDay;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ForecastDayDto> getForecastDay() {
        return forecastDay;
    }

    public void setForecastDay(List<ForecastDayDto> forecastDay) {
        this.forecastDay = forecastDay;
    }

}
