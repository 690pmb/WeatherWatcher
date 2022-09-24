package pmb.weatherwatcher.alert.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/** Monitored field database entity. */
@Entity
@Table(name = "monitored_field")
public class MonitoredField {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Field to monitor. */
  @Enumerated(EnumType.STRING)
  private WeatherField field;

  /** Value under this value will trigger an alert. */
  private Integer min;

  /** Value above this value will trigger an alert. */
  private Integer max;

  @JoinColumn(name = "alert", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Alert alert;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public WeatherField getField() {
    return field;
  }

  public void setField(WeatherField field) {
    this.field = field;
  }

  public Integer getMin() {
    return min;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public Alert getAlert() {
    return alert;
  }

  public void setAlert(Alert alert) {
    this.alert = alert;
  }
}
