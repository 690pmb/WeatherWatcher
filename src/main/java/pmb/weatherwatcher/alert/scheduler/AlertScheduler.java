package pmb.weatherwatcher.alert.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredFieldDto;
import pmb.weatherwatcher.alert.service.AlertService;
import pmb.weatherwatcher.notification.dto.Operation;
import pmb.weatherwatcher.notification.dto.PayloadDataDto;
import pmb.weatherwatcher.notification.dto.PayloadDto;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.service.NotificationService;
import pmb.weatherwatcher.notification.service.SubscriptionService;
import pmb.weatherwatcher.weather.dto.ForecastDayDto;
import pmb.weatherwatcher.weather.dto.ForecastDto;
import pmb.weatherwatcher.weather.dto.HourDto;
import pmb.weatherwatcher.weather.service.WeatherService;

@Component
public class AlertScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AlertScheduler.class);
  private static final Clock CLOCK = Clock.systemUTC();
  private static final String DETAIL_URL = "dashboard/details/%s?location=%s&alert=%s";
  private final AlertService alertService;
  private final WeatherService weatherService;
  private final SubscriptionService subscriptionService;
  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;

  public AlertScheduler(
      AlertService alertService,
      WeatherService weatherService,
      SubscriptionService subscriptionService,
      NotificationService notificationService,
      ObjectMapper objectMapper) {
    this.alertService = alertService;
    this.weatherService = weatherService;
    this.subscriptionService = subscriptionService;
    this.notificationService = notificationService;
    this.objectMapper = objectMapper;
  }

  @Scheduled(cron = "0 */15 * * * *")
  public void schedule() {
    LOGGER.debug("schedule");
    List<AlertDto> alertTriggered =
        this.alertService.findAllToTrigger(
            ZonedDateTime.now(CLOCK).getDayOfWeek(), ZonedDateTime.now(CLOCK));

    Map<String, ForecastDto> forecastByLocation =
        alertTriggered.stream()
            .collect(Collectors.groupingBy(AlertDto::getLocation))
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Entry::getKey,
                    e -> weatherService.findForecastByLocation(e.getKey(), 3, "fr")));

    Map<Pair<String, Long>, List<ForecastDayDto>> forecastDaysByUserToNotify =
        alertTriggered.stream().collect(Collectors.groupingBy(AlertDto::getUser)).values().stream()
            .map(
                alerts -> {
                  return alerts.stream()
                      .map(
                          a ->
                              Triple.of(
                                  a.getUser(),
                                  forecastDayToNotify(a, forecastByLocation.get(a.getLocation())),
                                  a.getId()))
                      .collect(Collectors.toSet());
                })
            .filter(Predicate.not(Set::isEmpty))
            .flatMap(Set::stream)
            .collect(Collectors.toMap(t -> Pair.of(t.getLeft(), t.getRight()), Triple::getMiddle));

    if (!forecastDaysByUserToNotify.isEmpty()) {
      Map<String, List<SubscriptionDto>> subsByUser =
          subscriptionService
              .findAllByUsers(
                  forecastDaysByUserToNotify.keySet().stream()
                      .map(Pair::getKey)
                      .collect(Collectors.toSet()))
              .stream()
              .collect(Collectors.groupingBy(SubscriptionDto::getUser));
      forecastDaysByUserToNotify
          .entrySet()
          .forEach(
              e ->
                  e.getValue()
                      .forEach(
                          day -> {
                            try {
                              notificationService.send(
                                  subsByUser.getOrDefault(
                                      e.getKey().getLeft(), Collections.emptyList()),
                                  buildPayload(
                                      day.getDate(), day.getLocation(), e.getKey().getRight()));
                            } catch (JsonProcessingException e1) {
                              LOGGER.error("Error when building payload: {}", e);
                            }
                          }));
    }
  }

  private static List<ForecastDayDto> forecastDayToNotify(AlertDto alert, ForecastDto forecastDto) {
    return forecastDto.getForecastDay().stream()
        .filter(
            forecastDay ->
                alert
                    .getMonitoredDays()
                    .contains(LocalDate.parse(forecastDay.getDate()).getDayOfWeek()))
        .peek(
            day ->
                day.setHour(
                    day.getHour().stream()
                        .filter(
                            h ->
                                BooleanUtils.isTrue(alert.getForceNotification())
                                    || isHourMonitored(h, alert))
                        .collect(Collectors.toList())))
        .filter(day -> !day.getHour().isEmpty())
        .filter(
            day ->
                day.getHour().stream()
                    .anyMatch(
                        h ->
                            alert.getMonitoredFields().stream()
                                .anyMatch(
                                    monitoredField ->
                                        BooleanUtils.isTrue(alert.getForceNotification())
                                            || isForecastHourToNotify(h, monitoredField))))
        .collect(Collectors.toList());
  }

  private static boolean isHourMonitored(HourDto hour, AlertDto alert) {
    return alert.getMonitoredHours().stream()
        .anyMatch(
            monitored ->
                ZonedDateTime.of(LocalDate.now(CLOCK), monitored, ZoneId.of(alert.getTimezone()))
                    .isEqual(hour.getZonedDateTime()));
  }

  private static boolean isForecastHourToNotify(HourDto h, MonitoredFieldDto monitoredField) {
    try {
      Field field = HourDto.class.getDeclaredField(monitoredField.getField().getCode());
      field.setAccessible(true);
      Object value = field.get(h);
      BigDecimal v = null;
      if (value instanceof Double) {
        v = BigDecimal.valueOf((Double) value);
      } else if (value instanceof Integer) {
        v = BigDecimal.valueOf((Integer) value);
      }
      return v != null
          && ((monitoredField.getMax() != null
                  && v.compareTo(BigDecimal.valueOf(monitoredField.getMax())) >= 0)
              || (monitoredField.getMin() != null
                  && v.compareTo(BigDecimal.valueOf(monitoredField.getMin())) <= 0));
    } catch (IllegalArgumentException
        | IllegalAccessException
        | NoSuchFieldException
        | SecurityException e) {
      LOGGER.error("Error when accessing field: {}", monitoredField.getField(), e);
      return false;
    }
  }

  private byte[] buildPayload(String date, String location, Long alertId)
      throws JsonProcessingException {
    return objectMapper.writeValueAsBytes(
        new PayloadDto(
            "Alerte Météo !",
            "Voir la météo en alerte",
            new PayloadDataDto(
                Operation.NAVIGATE_LAST_FOCUSED_OR_OPEN,
                String.format(DETAIL_URL, date, location, alertId))));
  }
}
