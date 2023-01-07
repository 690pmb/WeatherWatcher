package pmb.weatherwatcher.alert.dto;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/** Alert data, used to monitor weather. */
public class AlertDto {

  private Long id;

  /** Tells which days of week alerts are triggered. */
  @NotEmpty private Set<DayOfWeek> triggerDays;

  @NotNull private MonitoredDaysDto monitoredDays;

  /** Tells the time alerts are triggered. */
  @NotNull private OffsetTime triggerHour;

  /** Tells which hours are monitored. */
  @NotEmpty private Set<OffsetTime> monitoredHours;

  @NotEmpty private List<@Valid MonitoredFieldDto> monitoredFields;

  @NotBlank private String location;

  private Boolean forceNotification;

  private String user;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<DayOfWeek> getTriggerDays() {
    return triggerDays;
  }

  public void setTriggerDays(Set<DayOfWeek> triggerDays) {
    this.triggerDays = triggerDays;
  }

  public MonitoredDaysDto getMonitoredDays() {
    return monitoredDays;
  }

  public void setMonitoredDays(MonitoredDaysDto monitoredDays) {
    this.monitoredDays = monitoredDays;
  }

  public OffsetTime getTriggerHour() {
    return triggerHour;
  }

  public void setTriggerHour(OffsetTime triggerHour) {
    this.triggerHour = triggerHour;
  }

  public Set<OffsetTime> getMonitoredHours() {
    return monitoredHours;
  }

  public void setMonitoredHours(Set<OffsetTime> monitoredHours) {
    this.monitoredHours = monitoredHours;
  }

  public List<MonitoredFieldDto> getMonitoredFields() {
    return monitoredFields;
  }

  public void setMonitoredFields(List<MonitoredFieldDto> monitoredFields) {
    this.monitoredFields = monitoredFields;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Boolean getForceNotification() {
    return forceNotification;
  }

  public void setForceNotification(Boolean forceNotification) {
    this.forceNotification = forceNotification;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }
}
