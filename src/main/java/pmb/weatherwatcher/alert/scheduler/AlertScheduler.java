package pmb.weatherwatcher.alert.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredFieldDto;
import pmb.weatherwatcher.alert.service.AlertService;
import pmb.weatherwatcher.notification.dto.Operation;
import pmb.weatherwatcher.notification.dto.PayloadDataDto;
import pmb.weatherwatcher.notification.dto.PayloadDto;
import pmb.weatherwatcher.notification.service.NotificationService;
import pmb.weatherwatcher.notification.service.SubscriptionService;
import pmb.weatherwatcher.weather.dto.ForecastDayDto;
import pmb.weatherwatcher.weather.dto.ForecastDto;
import pmb.weatherwatcher.weather.dto.HourDto;
import pmb.weatherwatcher.weather.service.WeatherService;

@Component
public class AlertScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AlertScheduler.class);

  private final AlertService alertService;
  private final WeatherService weatherService;
  private final SubscriptionService subscriptionService;
  private final NotificationService notificationService;
  private static final Clock CLOCK = Clock.systemUTC();
  private static final Function<Integer, String> NOW_PLUS_DAYS =
      amountToAdd ->
          LocalDate.now(CLOCK).plusDays(amountToAdd).format(DateTimeFormatter.ISO_LOCAL_DATE);
  private byte[] payload;

  public AlertScheduler(
      AlertService alertService,
      WeatherService weatherService,
      SubscriptionService subscriptionService,
      NotificationService notificationService,
      ObjectMapper objectMapper)
      throws JsonProcessingException {
    this.alertService = alertService;
    this.weatherService = weatherService;
    this.subscriptionService = subscriptionService;
    this.notificationService = notificationService;
    payload =
        objectMapper.writeValueAsBytes(
            new PayloadDto(
                "Alerte Météo !",
                "Voir la météo en alerte",
                new PayloadDataDto(Operation.NAVIGATE_LAST_FOCUSED_OR_OPEN, "/dashboard")));
  }

  @Scheduled(cron = "0 */15 * * * *")
  public void schedule() {
    LOGGER.debug("schedule");
    Map<String, List<AlertDto>> alertTriggeredByLocation =
        this.alertService
            .findAllToTrigger(LocalDate.now(CLOCK).getDayOfWeek(), LocalTime.now(CLOCK))
            .stream()
            .collect(Collectors.groupingBy(AlertDto::getLocation));

    Map<ForecastDto, List<AlertDto>> alertTriggeredByForecast =
        alertTriggeredByLocation.entrySet().stream()
            .collect(
                Collectors.toMap(
                    e -> weatherService.findForecastbyLocation(e.getKey(), 3, "fr"),
                    Entry::getValue));

    Set<String> usersToNotify =
        alertTriggeredByForecast.entrySet().stream()
            .map(
                e -> {
                  Map<Long, Map<String, Boolean>> monitoredDays = buildMonitoredDays(e.getValue());
                  return e.getValue().stream()
                      .filter(a -> isAlertToNotify(a, e.getKey(), monitoredDays))
                      .map(AlertDto::getUser)
                      .distinct()
                      .collect(Collectors.toSet());
                })
            .distinct()
            .filter(Predicate.not(CollectionUtils::isEmpty))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    if (!usersToNotify.isEmpty()) {
      notificationService.send(subscriptionService.findAllByUsers(usersToNotify), payload);
    }
  }

  private static boolean isAlertToNotify(
      AlertDto alert, ForecastDto forecastDto, Map<Long, Map<String, Boolean>> monitoredDaysMap) {
    if (!alert.getForceNotification()) {
      return forecastDto.getForecastDay().stream()
          .filter(forecastDay -> monitoredDaysMap.get(alert.getId()).get(forecastDay.getDate()))
          .map(ForecastDayDto::getHour)
          .flatMap(List::stream)
          .filter(h -> isHourMonitored(h, alert))
          .anyMatch(
              h ->
                  alert.getMonitoredFields().stream()
                      .anyMatch(monitoredField -> isForecastHourToNotify(h, monitoredField)));
    }
    return true;
  }

  private static boolean isHourMonitored(HourDto hour, AlertDto alert) {
    return alert
        .getMonitoredHours()
        .contains(
            OffsetTime.of(
                LocalTime.parse(hour.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                ZoneOffset.UTC));
  }

  private static boolean isForecastHourToNotify(HourDto h, MonitoredFieldDto monitoredField) {
    try {
      Field field = HourDto.class.getDeclaredField(monitoredField.getField().getCode());
      field.setAccessible(true);
      Object value = field.get(h);
      BigDecimal v = BigDecimal.ZERO;
      if (value instanceof Double) {
        v = BigDecimal.valueOf(((Double) value).doubleValue());
      } else if (value instanceof Integer) {
        v = BigDecimal.valueOf(((Integer) value).intValue());
      }
      return (monitoredField.getMax() != null
              && v.compareTo(BigDecimal.valueOf(monitoredField.getMax())) >= 0)
          || (monitoredField.getMin() != null
              && v.compareTo(BigDecimal.valueOf(monitoredField.getMin())) <= 0);
    } catch (IllegalArgumentException
        | IllegalAccessException
        | NoSuchFieldException
        | SecurityException e) {
      LOGGER.error("Error when accessing field: {}", monitoredField.getField(), e);
      return false;
    }
  }

  private Map<Long, Map<String, Boolean>> buildMonitoredDays(List<AlertDto> alerts) {
    return alerts.stream()
        .map(
            alert ->
                Pair.of(
                    alert.getId(),
                    Map.of(
                        NOW_PLUS_DAYS.apply(0),
                        alert.getMonitoredDays().getSameDay(),
                        NOW_PLUS_DAYS.apply(1),
                        alert.getMonitoredDays().getNextDay(),
                        NOW_PLUS_DAYS.apply(2),
                        alert.getMonitoredDays().getTwoDayLater())))
        .collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getValue()));
  }
}
