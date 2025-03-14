package pmb.weatherwatcher.alert;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredFieldDto;
import pmb.weatherwatcher.alert.model.WeatherField;

public final class AlertUtils {

  private AlertUtils() {}

  public static AlertDto buildAlertDto(
      Long id,
      Set<DayOfWeek> triggerDays,
      LocalTime triggerHour,
      Set<DayOfWeek> monitoredDays,
      Set<LocalTime> monitoredHours,
      List<MonitoredFieldDto> monitoredFields,
      String location,
      Boolean force,
      String user,
      String timezone) {
    AlertDto alert = new AlertDto();
    alert.setId(id);
    alert.setTriggerDays(Optional.ofNullable(triggerDays).map(HashSet::new).orElse(null));
    alert.setTriggerHour(triggerHour);
    alert.setMonitoredDays(monitoredDays);
    alert.setMonitoredHours(monitoredHours);
    alert.setMonitoredFields(monitoredFields);
    alert.setLocation(location);
    alert.setForceNotification(force);
    alert.setUser(user);
    alert.setTimezone(timezone);
    return alert;
  }

  public static MonitoredFieldDto buildMonitoredFieldDto(
      Long id, WeatherField field, Integer min, Integer max) {
    MonitoredFieldDto monitoredFieldDto = new MonitoredFieldDto();
    monitoredFieldDto.setId(id);
    monitoredFieldDto.setField(field);
    monitoredFieldDto.setMin(min);
    monitoredFieldDto.setMax(max);
    return monitoredFieldDto;
  }
}
