package pmb.weatherwatcher.weather.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location implements java.io.Serializable {

  private static final long serialVersionUID = -988571205296280554L;
  private String name;
  private String region;
  private String country;
  private Double lat;
  private Double lon;

  @JsonProperty("tz_id")
  private String tzId;

  @JsonProperty("localtime_epoch")
  private Integer localtimeEpoch;

  private String localtime;

  /** GETTER Local area name. */
  public String getName() {
    return name;
  }

  /** SETTER Local area name. */
  public void setName(String value) {
    name = value;
  }

  /** GETTER Local area region. */
  public String getRegion() {
    return region;
  }

  /** SETTER Local area region. */
  public void setRegion(String value) {
    region = value;
  }

  /** GETTER Country */
  public String getCountry() {
    return country;
  }

  /** SETTER Country */
  public void setCountry(String value) {
    country = value;
  }

  /** GETTER Area latitude */
  public Double getLat() {
    return lat;
  }

  /** SETTER Area latitude */
  public void setLat(Double value) {
    lat = value;
  }

  /** GETTER Area longitude */
  public Double getLon() {
    return lon;
  }

  /** SETTER Area longitude */
  public void setLon(Double value) {
    lon = value;
  }

  /** GETTER Time zone */
  public String getTzId() {
    return tzId;
  }

  /** SETTER Time zone */
  public void setTzId(String value) {
    tzId = value;
  }

  /** GETTER Local date and time in unix time */
  public Integer getLocaltimeEpoch() {
    return localtimeEpoch;
  }

  /** SETTER Local date and time in unix time */
  public void setLocaltimeEpoch(Integer value) {
    localtimeEpoch = value;
  }

  /** GETTER Local date and time */
  public String getLocaltime() {
    return localtime;
  }

  /** SETTER Local date and time */
  public void setLocaltime(String value) {
    localtime = value;
  }
}
