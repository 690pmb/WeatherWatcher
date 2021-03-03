package pmb.weatherwatcher.dto.weather;

import java.util.List;

import pmb.weatherwatcher.weatherapi.model.Astro;
import pmb.weatherwatcher.weatherapi.model.Day;

public class ForecastDayDto {

    private String date;
    private Day day;
    private Astro astro;
    private List<HourDto> hour;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Astro getAstro() {
        return astro;
    }

    public void setAstro(Astro astro) {
        this.astro = astro;
    }

    public List<HourDto> getHour() {
        return hour;
    }

    public void setHour(List<HourDto> hour) {
        this.hour = hour;
    }

}
