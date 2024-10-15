package pmb.weatherwatcher.weather.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hour implements java.io.Serializable {

  private static final long serialVersionUID = -4742803188539272633L;

  @JsonProperty("time_epoch")
  private Integer timeEpoch;

  private String time;

  @JsonProperty("temp_c")
  private Double tempC;

  @JsonProperty("is_day")
  private Integer isDay;

  private Condition condition;

  @JsonProperty("wind_kph")
  private Double windKph;

  @JsonProperty("wind_degree")
  private Integer windDegree;

  @JsonProperty("wind_dir")
  private Direction windDir;

  @JsonProperty("pressure_mb")
  private Double pressureMb;

  @JsonProperty("precip_mm")
  private Double precipMm;

  private Integer humidity;
  private Integer cloud;

  @JsonProperty("feelslike_c")
  private Double feelsLikeC;

  @JsonProperty("windchill_c")
  private Double windChillC;

  @JsonProperty("heatindex_c")
  private Double heatIndexC;

  @JsonProperty("dewpoint_c")
  private Double dewPointC;

  @JsonProperty("will_it_rain")
  private Integer willItRain;

  @JsonProperty("chance_of_rain")
  private Integer chanceOfRain;

  @JsonProperty("will_it_snow")
  private Integer willItSnow;

  @JsonProperty("chance_of_snow")
  private Integer chanceOfSnow;

  @JsonProperty("vis_km")
  private Double visKm;

  private Double uv;

  @JsonProperty("gust_kph")
  private Double gustKph;

  /** GETTER Time as epoch */
  public Integer getTimeEpoch() {
    return timeEpoch;
  }

  /** SETTER Time as epoch */
  public void setTimeEpoch(Integer value) {
    timeEpoch = value;
  }

  /** GETTER Date and time */
  public String getTime() {
    return time;
  }

  /** SETTER Date and time */
  public void setTime(String value) {
    time = value;
  }

  /** GETTER Temperature in celsius */
  public Double getTempC() {
    return tempC;
  }

  /** SETTER Temperature in celsius */
  public void setTempC(Double value) {
    tempC = value;
  }

  /**
   * GETTER 1 = Yes 0 = No <br>
   * Whether to show day condition icon or night icon
   */
  public Integer getIsDay() {
    return isDay;
  }

  /**
   * SETTER 1 = Yes 0 = No <br>
   * Whether to show day condition icon or night icon
   */
  public void setIsDay(Integer value) {
    isDay = value;
  }

  public Condition getCondition() {
    return condition;
  }

  public void setCondition(Condition value) {
    condition = value;
  }

  /** GETTER Wind speed in kilometer per hour */
  public Double getWindKph() {
    return windKph;
  }

  /** SETTER Wind speed in kilometer per hour */
  public void setWindKph(Double value) {
    windKph = value;
  }

  /** GETTER Wind direction in degrees */
  public Integer getWindDegree() {
    return windDegree;
  }

  /** SETTER Wind direction in degrees */
  public void setWindDegree(Integer value) {
    windDegree = value;
  }

  /** GETTER Wind direction as 16 point compass. e.g.: NSW */
  public Direction getWindDir() {
    return windDir;
  }

  /** SETTER Wind direction as 16 point compass. e.g.: NSW */
  public void setWindDir(Direction value) {
    windDir = value;
  }

  /** GETTER Pressure in millibars */
  public Double getPressureMb() {
    return pressureMb;
  }

  /** SETTER Pressure in millibars */
  public void setPressureMb(Double value) {
    pressureMb = value;
  }

  /** GETTER Precipitation amount in millimeters */
  public Double getPrecipMm() {
    return precipMm;
  }

  /** SETTER Precipitation amount in millimeters */
  public void setPrecipMm(Double value) {
    precipMm = value;
  }

  /** GETTER Humidity as percentage */
  public Integer getHumidity() {
    return humidity;
  }

  /** SETTER Humidity as percentage */
  public void setHumidity(Integer value) {
    humidity = value;
  }

  /** GETTER Cloud cover as percentage */
  public Integer getCloud() {
    return cloud;
  }

  /** SETTER Cloud cover as percentage */
  public void setCloud(Integer value) {
    cloud = value;
  }

  /** GETTER Feels like temperature as celcius */
  public Double getFeelsLikeC() {
    return feelsLikeC;
  }

  /** SETTER Feels like temperature as celcius */
  public void setFeelsLikeC(Double value) {
    feelsLikeC = value;
  }

  public Double getWindChillC() {
    return windChillC;
  }

  public void setWindChillC(Double windchillC) {
    windChillC = windchillC;
  }

  public Double getHeatIndexC() {
    return heatIndexC;
  }

  public void setHeatIndexC(Double heatindexC) {
    heatIndexC = heatindexC;
  }

  public Double getDewPointC() {
    return dewPointC;
  }

  public void setDewPointC(Double dewpointC) {
    dewPointC = dewpointC;
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

  public Double getVisKm() {
    return visKm;
  }

  public void setVisKm(Double value) {
    visKm = value;
  }

  /** GETTER UV Index */
  public Double getUv() {
    return uv;
  }

  /** SETTER UV Index */
  public void setUv(Double value) {
    uv = value;
  }

  /** GETTER Wind gust in kilometer per hour */
  public Double getGustKph() {
    return gustKph;
  }

  /** SETTER Wind gust in kilometer per hour */
  public void setGustKph(Double value) {
    gustKph = value;
  }
}
