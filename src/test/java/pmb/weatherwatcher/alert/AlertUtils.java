package pmb.weatherwatcher.alert;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredDaysDto;
import pmb.weatherwatcher.alert.dto.MonitoredFieldDto;
import pmb.weatherwatcher.alert.model.WeatherField;

public final class AlertUtils {

  private AlertUtils() {}

  public static AlertDto buildAlertDto(
      Long id,
      Set<DayOfWeek> triggerDays,
      OffsetTime triggerHour,
      MonitoredDaysDto monitoredDays,
      Set<OffsetTime> monitoredHours,
      List<MonitoredFieldDto> monitoredFields,
      String location,
      Boolean force,
      String user) {
    AlertDto alert = new AlertDto();
    alert.setId(id);
    alert.setTriggerDays(Optional.ofNullable(triggerDays).map(LinkedHashSet::new).orElse(null));
    alert.setTriggerHour(triggerHour);
    alert.setMonitoredDays(monitoredDays);
    alert.setMonitoredHours(monitoredHours);
    alert.setMonitoredFields(monitoredFields);
    alert.setLocation(location);
    alert.setForceNotification(force);
    alert.setUser(user);
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

  public static MonitoredDaysDto buildMonitoredDaysDto(Boolean same, Boolean next, Boolean two) {
    MonitoredDaysDto monitoredDaysDto = new MonitoredDaysDto();
    monitoredDaysDto.setSameDay(same);
    monitoredDaysDto.setNextDay(next);
    monitoredDaysDto.setTwoDayLater(two);
    return monitoredDaysDto;
  }
}
