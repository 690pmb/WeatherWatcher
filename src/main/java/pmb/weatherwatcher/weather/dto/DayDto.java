package pmb.weatherwatcher.weather.dto;

import pmb.weatherwatcher.weather.api.model.Condition;

public class DayDto {

    private Double maxTempC;
    private Double minTempC;
    private Double avgTempC;
    private Double maxWindKph;
    private Double totalPrecipMm;
    private Double avgVisKm;
    private Double avgHumidity;
    private Condition condition;
    private Double uv;
    private Integer dailyWillItRain;
    private Integer dailyChanceOfRain;
    private Integer dailyWillItSnow;
    private Integer dailyChanceOfSnow;

    /**
     * GETTER Maximum temperature in celsius for the day.
     */
    public Double getMaxTempC() {
        return maxTempC;
    }

    /**
     * SETTER Maximum temperature in celsius for the day.
     */
    public void setMaxTempC(Double value) {
        maxTempC = value;
    }

    /**
     * GETTER Minimum temperature in celsius for the day
     */
    public Double getMinTempC() {
        return minTempC;
    }

    /**
     * SETTER Minimum temperature in celsius for the day
     */
    public void setMinTempC(Double value) {
        minTempC = value;
    }

    /**
     * GETTER Average temperature in celsius for the day
     */
    public Double getAvgTempC() {
        return avgTempC;
    }

    /**
     * SETTER Average temperature in celsius for the day
     */
    public void setAvgTempC(Double value) {
        avgTempC = value;
    }

    /**
     * GETTER Maximum wind speed in kilometer per hour
     */
    public Double getMaxWindKph() {
        return maxWindKph;
    }

    /**
     * SETTER Maximum wind speed in kilometer per hour
     */
    public void setMaxWindKph(Double value) {
        maxWindKph = value;
    }

    /**
     * GETTER Total precipitation in milimeter
     */
    public Double getTotalPrecipMm() {
        return totalPrecipMm;
    }

    /**
     * SETTER Total precipitation in milimeter
     */
    public void setTotalPrecipMm(Double value) {
        totalPrecipMm = value;
    }

    /**
     * GETTER Average visibility in kilometer
     */
    public Double getAvgVisKm() {
        return avgVisKm;
    }

    /**
     * SETTER Average visibility in kilometer
     */
    public void setAvgVisKm(Double value) {
        avgVisKm = value;
    }

    /**
     * GETTER Average humidity as percentage
     */
    public Double getAvgHumidity() {
        return avgHumidity;
    }

    /**
     * SETTER Average humidity as percentage
     */
    public void setAvgHumidity(Double value) {
        avgHumidity = value;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition value) {
        condition = value;
    }

    /**
     * GETTER UV Index
     */
    public Double getUv() {
        return uv;
    }

    /**
     * SETTER UV Index
     */
    public void setUv(Double value) {
        uv = value;
    }

    public Integer getDailyWillItRain() {
        return dailyWillItRain;
    }

    public void setDailyWillItRain(Integer dailyWillItRain) {
        this.dailyWillItRain = dailyWillItRain;
    }

    public Integer getDailyChanceOfRain() {
        return dailyChanceOfRain;
    }

    public void setDailyChanceOfRain(Integer dailyChanceOfRain) {
        this.dailyChanceOfRain = dailyChanceOfRain;
    }

    public Integer getDailyWillItSnow() {
        return dailyWillItSnow;
    }

    public void setDailyWillItSnow(Integer dailyWillItSnow) {
        this.dailyWillItSnow = dailyWillItSnow;
    }

    public Integer getDailyChanceOfSnow() {
        return dailyChanceOfSnow;
    }

    public void setDailyChanceOfSnow(Integer dailyChanceOfSnow) {
        this.dailyChanceOfSnow = dailyChanceOfSnow;
    }

}
