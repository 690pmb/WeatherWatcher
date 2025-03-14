package pmb.weatherwatcher.alert.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/** Alert data, used to monitor weather. */
public class AlertDto {

  private Long id;

  /** Tells which days of week alerts are triggered. */
  @NotEmpty private Set<DayOfWeek> triggerDays;

  /** Tells which days are monitored. */
  @NotEmpty private Set<DayOfWeek> monitoredDays;

  /** Tells the time alerts are triggered. */
  @NotNull private LocalTime triggerHour;

  /** Tells which hours are monitored. */
  @NotEmpty private Set<LocalTime> monitoredHours;

  @NotEmpty private List<@Valid MonitoredFieldDto> monitoredFields;

  @NotBlank private String location;

  private Boolean forceNotification;

  private String user;

  @Null @JsonIgnore private String timezone;

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

  public Set<DayOfWeek> getMonitoredDays() {
    return monitoredDays;
  }

  public void setMonitoredDays(Set<DayOfWeek> monitoredDays) {
    this.monitoredDays = monitoredDays;
  }

  public LocalTime getTriggerHour() {
    return triggerHour;
  }

  public void setTriggerHour(LocalTime triggerHour) {
    this.triggerHour = triggerHour;
  }

  public Set<LocalTime> getMonitoredHours() {
    return monitoredHours;
  }

  public void setMonitoredHours(Set<LocalTime> monitoredHours) {
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

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
}
