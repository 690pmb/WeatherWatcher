package pmb.weatherwatcher.weather.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Forecastday implements java.io.Serializable {

  private static final long serialVersionUID = 5397660598274391661L;
  private String date;

  @JsonProperty("date_epoch")
  private Integer dateEpoch;

  private Day day;
  private List<Hour> hour;
  private Astro astro;

  /** GETTER Forecast date */
  public String getDate() {
    return date;
  }

  /** SETTER Forecast date */
  public void setDate(String value) {
    date = value;
  }

  /** GETTER Forecast date as unix time. */
  public Integer getDateEpoch() {
    return dateEpoch;
  }

  /** SETTER Forecast date as unix time. */
  public void setDateEpoch(Integer value) {
    dateEpoch = value;
  }

  /** GETTER See day element */
  public Day getDay() {
    return day;
  }

  /** SETTER See day element */
  public void setDay(Day value) {
    day = value;
  }

  /** GETTER See hour element */
  public List<Hour> getHour() {
    return hour;
  }

  /** SETTER See hour element */
  public void setHour(List<Hour> hour) {
    this.hour = hour;
  }

  public Astro getAstro() {
    return astro;
  }

  public void setAstro(Astro value) {
    astro = value;
  }
}
