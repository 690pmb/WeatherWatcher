package pmb.weatherwatcher.alert;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.Set;

import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredDaysDto;
import pmb.weatherwatcher.alert.dto.MonitoredFieldDto;
import pmb.weatherwatcher.alert.model.WeatherField;

public final class AlertUtils {

    private AlertUtils() {}

    public static AlertDto buildAlertDto(Long id, Set<DayOfWeek> triggerDays, OffsetTime triggerHour, MonitoredDaysDto monitoredDays,
            List<OffsetTime> monitoredHours, List<MonitoredFieldDto> monitoredFields, String location, Boolean force) {
        AlertDto alert = new AlertDto();
        alert.setId(id);
        alert.setTriggerDays(triggerDays);
        alert.setTriggerHour(triggerHour);
        alert.setMonitoredDays(monitoredDays);
        alert.setMonitoredHours(monitoredHours);
        alert.setMonitoredFields(monitoredFields);
        alert.setLocation(location);
        alert.setForceNotification(force);
        return alert;
    }

    public static MonitoredFieldDto buildMonitoredFieldDto(Long id, WeatherField field, Integer min, Integer max) {
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
