package pmb.weatherwatcher.weather.dto;

import java.time.ZonedDateTime;
import pmb.weatherwatcher.weather.api.model.Condition;
import pmb.weatherwatcher.weather.api.model.Direction;

public class HourDto {

  private String time;
  private ZonedDateTime zonedDateTime;
  private Double tempC;
  private Boolean isDay;
  private Condition condition;
  private Double windKph;
  private Direction windDir;
  private Double pressureMb;
  private Double precipMm;
  private Integer humidity;
  private Integer cloud;
  private Double feelsLikeC;
  private Integer willItRain;
  private Integer chanceOfRain;
  private Integer willItSnow;
  private Integer chanceOfSnow;
  private Double uv;

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public ZonedDateTime getZonedDateTime() {
    return zonedDateTime;
  }

  public void setZonedDateTime(ZonedDateTime zonedDateTime) {
    this.zonedDateTime = zonedDateTime;
  }

  public Double getTempC() {
    return tempC;
  }

  public void setTempC(Double tempC) {
    this.tempC = tempC;
  }

  public Boolean getIsDay() {
    return isDay;
  }

  public void setIsDay(Boolean isDay) {
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

  public Direction getWindDir() {
    return windDir;
  }

  public void setWindDir(Direction windDir) {
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
