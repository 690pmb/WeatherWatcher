package pmb.weatherwatcher.alert.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/** Tells which of the next three days are to be monitored. */
@Embeddable
public class MonitoredDays {

  @Column(name = "same_day")
  private Boolean sameDay;

  @Column(name = "next_day")
  private Boolean nextDay;

  @Column(name = "two_day_later")
  private Boolean twoDayLater;

  public Boolean getSameDay() {
    return sameDay;
  }

  public void setSameDay(Boolean sameDay) {
    this.sameDay = sameDay;
  }

  public Boolean getNextDay() {
    return nextDay;
  }

  public void setNextDay(Boolean nextDay) {
    this.nextDay = nextDay;
  }

  public Boolean getTwoDayLater() {
    return twoDayLater;
  }

  public void setTwoDayLater(Boolean twoDayLater) {
    this.twoDayLater = twoDayLater;
  }
}
