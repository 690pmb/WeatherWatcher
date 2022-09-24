package pmb.weatherwatcher.alert.dto;

import javax.validation.constraints.NotNull;
import pmb.weatherwatcher.alert.model.WeatherField;

/** Monitor a field with min and max values. */
public class MonitoredFieldDto {

  private Long id;

  /** Field to monitor. */
  @NotNull private WeatherField field;

  /** Value under this value will trigger an alert. */
  private Integer min;

  /** Value above this value will trigger an alert. */
  private Integer max;

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
}
