package pmb.weatherwatcher.weather.dto;

import java.util.List;

import pmb.weatherwatcher.weather.api.model.Astro;

public class ForecastDayDto {

    private String date;
    private DayDto day;
    private Astro astro;
    private List<HourDto> hour;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DayDto getDay() {
        return day;
    }

    public void setDay(DayDto day) {
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
