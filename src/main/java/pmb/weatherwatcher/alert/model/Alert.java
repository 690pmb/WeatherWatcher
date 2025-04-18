package pmb.weatherwatcher.alert.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.BatchSize;
import pmb.weatherwatcher.user.model.User;

/** Alert database entity, monitor settings. */
@Entity
@Table(name = "alert")
public class Alert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Tells which days of week alerts are triggered. */
  @Column(name = "day")
  @Enumerated(EnumType.STRING)
  @CollectionTable(
      name = "trigger_day",
      joinColumns = @JoinColumn(name = "alert", referencedColumnName = "id"))
  @ElementCollection(targetClass = DayOfWeek.class)
  private Set<DayOfWeek> triggerDays;

  /** Tells which days are monitored. */
  @Column(name = "day")
  @Enumerated(EnumType.STRING)
  @CollectionTable(
      name = "monitored_day",
      joinColumns = @JoinColumn(name = "alert", referencedColumnName = "id"))
  @ElementCollection(targetClass = DayOfWeek.class)
  private Set<DayOfWeek> monitoredDays;

  /** Tells the time alerts are triggered. */
  @Column(name = "trigger_hour")
  private LocalTime triggerHour;

  /** Tells which hours are monitored. */
  @Column(name = "hour")
  @CollectionTable(
      name = "alert_monitored_hour",
      joinColumns = @JoinColumn(name = "alert", referencedColumnName = "id"))
  @ElementCollection(targetClass = LocalTime.class)
  private Set<LocalTime> monitoredHours;

  @BatchSize(size = 10)
  @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MonitoredField> monitoredFields;

  private String location;

  @Column(name = "force_notification")
  private Boolean forceNotification;

  @JoinColumn(name = "user", referencedColumnName = "login")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private User user;

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

  public List<MonitoredField> getMonitoredFields() {
    return monitoredFields;
  }

  public void setMonitoredFields(List<MonitoredField> monitoredFields) {
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
