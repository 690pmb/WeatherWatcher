package pmb.weatherwatcher.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import pmb.weatherwatcher.weatherapi.model.Condition;

public class HourDto
        implements java.io.Serializable {

    private static final long serialVersionUID = -4742803188539272633L;
    private String time;
    @JsonProperty("temp_c")
    private Double tempC;
    @JsonProperty("is_day")
    private Integer isDay;
    private Condition condition;
    @JsonProperty("wind_kph")
    private Double windKph;
    @JsonProperty("wind_dir")
    private String windDir;
    @JsonProperty("pressure_mb")
    private Double pressureMb;
    @JsonProperty("precip_mm")
    private Double precipMm;
    private Integer humidity;
    private Integer cloud;
    @JsonProperty("feelslike_c")
    private Double feelsLikeC;
    @JsonProperty("will_it_rain")
    private Integer willItRain;
    @JsonProperty("chance_of_rain")
    private Integer chanceOfRain;
    @JsonProperty("will_it_snow")
    private Integer willItSnow;
    @JsonProperty("chance_of_snow")
    private Integer chanceOfSnow;
    private Double uv;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getTempC() {
        return tempC;
    }

    public void setTempC(Double tempC) {
        this.tempC = tempC;
    }

    public Integer getIsDay() {
        return isDay;
    }

    public void setIsDay(Integer isDay) {
        this.isDay = isDay;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Double getWindKph() {
        return windKph;
    }

    public void setWindKph(Double windKph) {
        this.windKph = windKph;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public Double getPressureMb() {
        return pressureMb;
    }

    public void setPressureMb(Double pressureMb) {
        this.pressureMb = pressureMb;
    }

    public Double getPrecipMm() {
        return precipMm;
    }

    public void setPrecipMm(Double precipMm) {
        this.precipMm = precipMm;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getCloud() {
        return cloud;
    }

    public void setCloud(Integer cloud) {
        this.cloud = cloud;
    }

    public Double getFeelsLikeC() {
        return feelsLikeC;
    }

    public void setFeelsLikeC(Double feelsLikeC) {
        this.feelsLikeC = feelsLikeC;
    }

    public Integer getWillItRain() {
        return willItRain;
    }

    public void setWillItRain(Integer willItRain) {
        this.willItRain = willItRain;
    }

    public Integer getChanceOfRain() {
        return chanceOfRain;
    }

    public void setChanceOfRain(Integer chanceOfRain) {
        this.chanceOfRain = chanceOfRain;
    }

    public Integer getWillItSnow() {
        return willItSnow;
    }

    public void setWillItSnow(Integer willItSnow) {
        this.willItSnow = willItSnow;
    }

    public Integer getChanceOfSnow() {
        return chanceOfSnow;
    }

    public void setChanceOfSnow(Integer chanceOfSnow) {
        this.chanceOfSnow = chanceOfSnow;
    }

    public Double getUv() {
        return uv;
    }

    public void setUv(Double uv) {
        this.uv = uv;
    }

}
