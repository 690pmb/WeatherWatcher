package pmb.weatherwatcher.weather.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Current
        implements java.io.Serializable {

    private static final long serialVersionUID = -5475354829602088755L;
    @JsonProperty("last_updated_epoch")
    private Integer lastUpdatedEpoch;
    @JsonProperty("last_updated")
    private String lastUpdated;
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
    private String windDir;
    @JsonProperty("pressure_mb")
    private Double pressureMb;
    @JsonProperty("precip_mm")
    private Double precipMm;
    private Integer humidity;
    private Integer cloud;
    @JsonProperty("feelslike_c")
    private Double feelsLikeC;
    @JsonProperty("vis_km")
    private Double visKm;
    private Double uv;
    @JsonProperty("gust_kph")
    private Double gustKph;

    /**
     * GETTER Local time when the real time data was updated in unix time.
     */
    public Integer getLastUpdatedEpoch() {
        return lastUpdatedEpoch;
    }

    /**
     * SETTER Local time when the real time data was updated in unix time.
     */
    public void setLastUpdatedEpoch(Integer value) {
        lastUpdatedEpoch = value;
    }

    /**
     * GETTER Local time when the real time data was updated.
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * SETTER Local time when the real time data was updated.
     */
    public void setLastUpdated(String value) {
        lastUpdated = value;
    }

    /**
     * GETTER Temperature in celsius
     */
    public Double getTempC() {
        return tempC;
    }

    /**
     * SETTER Temperature in celsius
     */
    public void setTempC(Double value) {
        tempC = value;
    }

    /**
     * GETTER 1 = Yes 0 = No <br />
     * Whether to show day condition icon or night icon
     */
    public Integer getIsDay() {
        return isDay;
    }

    /**
     * SETTER 1 = Yes 0 = No <br />
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

    /**
     * GETTER Wind speed in kilometer per hour
     */
    public Double getWindKph() {
        return windKph;
    }

    /**
     * SETTER Wind speed in kilometer per hour
     */
    public void setWindKph(Double value) {
        windKph = value;
    }

    /**
     * GETTER Wind direction in degrees
     */
    public Integer getWindDegree() {
        return windDegree;
    }

    /**
     * SETTER Wind direction in degrees
     */
    public void setWindDegree(Integer value) {
        windDegree = value;
    }

    /**
     * GETTER Wind direction as 16 point compass. e.g.: NSW
     */
    public String getWindDir() {
        return windDir;
    }

    /**
     * SETTER Wind direction as 16 point compass. e.g.: NSW
     */
    public void setWindDir(String value) {
        windDir = value;
    }

    /**
     * GETTER Pressure in millibars
     */
    public Double getPressureMb() {
        return pressureMb;
    }

    /**
     * SETTER Pressure in millibars
     */
    public void setPressureMb(Double value) {
        pressureMb = value;
    }

    /**
     * GETTER Precipitation amount in millimeters
     */
    public Double getPrecipMm() {
        return precipMm;
    }

    /**
     * SETTER Precipitation amount in millimeters
     */
    public void setPrecipMm(Double value) {
        precipMm = value;
    }

    /**
     * GETTER Humidity as percentage
     */
    public Integer getHumidity() {
        return humidity;
    }

    /**
     * SETTER Humidity as percentage
     */
    public void setHumidity(Integer value) {
        humidity = value;
    }

    /**
     * GETTER Cloud cover as percentage
     */
    public Integer getCloud() {
        return cloud;
    }

    /**
     * SETTER Cloud cover as percentage
     */
    public void setCloud(Integer value) {
        cloud = value;
    }

    /**
     * GETTER Feels like temperature as celcius
     */
    public Double getFeelsLikeC() {
        return feelsLikeC;
    }

    /**
     * SETTER Feels like temperature as celcius
     */
    public void setFeelsLikeC(Double value) {
        feelsLikeC = value;
    }

    public Double getVisKm() {
        return visKm;
    }

    public void setVisKm(Double value) {
        visKm = value;
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

    /**
     * GETTER Wind gust in kilometer per hour
     */
    public Double getGustKph() {
        return gustKph;
    }

    /**
     * SETTER Wind gust in kilometer per hour
     */
    public void setGustKph(Double value) {
        gustKph = value;
    }

}
