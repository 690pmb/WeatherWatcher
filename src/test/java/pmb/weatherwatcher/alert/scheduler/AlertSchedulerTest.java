package pmb.weatherwatcher.alert.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.alert.AlertUtils;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.model.WeatherField;
import pmb.weatherwatcher.alert.service.AlertService;
import pmb.weatherwatcher.notification.NotificationUtils;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.service.NotificationService;
import pmb.weatherwatcher.notification.service.SubscriptionService;
import pmb.weatherwatcher.weather.WeatherUtils;
import pmb.weatherwatcher.weather.service.WeatherService;

@ServiceTestRunner
@Import({AlertScheduler.class, ObjectMapper.class})
class AlertSchedulerTest {
  @MockBean AlertService alertService;
  @MockBean WeatherService weatherService;
  @MockBean SubscriptionService subscriptionService;
  @MockBean NotificationService notificationService;
  @Autowired AlertScheduler alertScheduler;
  @Captor ArgumentCaptor<List<SubscriptionDto>> sentSubscriptions;
  private static final String TZ = "Europe/Paris";
  private static final String PAYLOAD =
      "{\"notification\":{\"title\":\"Alerte Météo !\",\"body\":\"Voir la météo en"
          + " alerte\",\"data\":{\"onActionClick\":{\"default\":{\"operation\":\"navigateLastFocusedOrOpen\",\"url\":\"dashboard/details/%s?location=%s&alert=%s\"}}},\"requireInteraction\":true}}";

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2022-10-22T10:00:00.00Z"), ZoneOffset.UTC);
  private static final ZonedDateTime DUMMY_ZONED_TIME =
      ZonedDateTime.of(LocalDate.of(2022, 10, 22), LocalTime.of(10, 0, 0), ZoneId.of("Z"));

  @BeforeAll
  static void setupClock() {
    Mockito.mockStatic(Clock.class).when(Clock::systemUTC).thenReturn(CLOCK);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(
        alertService, weatherService, subscriptionService, notificationService);
  }

  @Test
  void given_no_alert_triggered_then_no_notification() {
    when(alertService.findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_ZONED_TIME))
        .thenReturn(Collections.emptyList());

    alertScheduler.schedule();

    verify(alertService).findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_ZONED_TIME);
    verify(weatherService, never()).findForecastByLocation(any(), any(), any());
    verify(subscriptionService, never()).findAllByUsers(any());
    verify(notificationService, never()).send(any(), any());
  }

  /**
   * Mocked data Alerts: alert1: lyon, user1, saturday, feels like 10<x>20, [10h] alert2: lyon,
   * user1, all days, chance rain 10<x>60, [10, 20h, 17h] alert3: lyon, user2, saturday, feels fike,
   * 10<x>30, [10h], force alert4: paris, user3, saturday and monday, rain 10<x>60, [14h, 17h]
   *
   * <p>Forecast Weather lyon: - today 10h, feels like 25° - tomorrow 10h rain 30 Paris: - today 14h
   * feels like 25 & rain 70
   *
   * <p>Notification that should be sent: - alert1: today, lyon, user1 - alert2: non - alert3:
   * today, lyon, user2 - alert4: today, paris, user4
   */
  @Test
  void given_alert_then_notification() {
    AlertDto alert1 =
        AlertUtils.buildAlertDto(
            1L,
            null,
            null,
            Set.of(DayOfWeek.SATURDAY),
            Set.of(LocalTime.of(10, 0, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 20)),
            "Lyon",
            false,
            "user1",
            TZ);
    AlertDto alert2 =
        AlertUtils.buildAlertDto(
            2L,
            null,
            null,
            Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY),
            Set.of(LocalTime.of(10, 0, 0), LocalTime.of(20, 0, 0), LocalTime.of(17, 0, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.CHANCE_OF_RAIN, 10, 60)),
            "Lyon",
            false,
            "user1",
            TZ);
    AlertDto alert3 =
        AlertUtils.buildAlertDto(
            3L,
            null,
            null,
            Set.of(DayOfWeek.SATURDAY),
            Set.of(LocalTime.of(10, 0, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 30)),
            "Lyon",
            true,
            "user2",
            TZ);
    AlertDto alert4 =
        AlertUtils.buildAlertDto(
            4L,
            null,
            null,
            Set.of(DayOfWeek.SATURDAY, DayOfWeek.MONDAY),
            Set.of(LocalTime.of(14, 0, 0), LocalTime.of(17, 0, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.CHANCE_OF_RAIN, 10, 60)),
            "Paris",
            false,
            "user3",
            TZ);
    SubscriptionDto sub1 =
        NotificationUtils.buildSubscriptionDto("ua1", "end1", "pk1", "pk11", null, "user1");
    SubscriptionDto sub2 =
        NotificationUtils.buildSubscriptionDto("ua2", "end2", "pk2", "pk22", null, "user2");
    SubscriptionDto sub3 =
        NotificationUtils.buildSubscriptionDto("ua3", "end3", "pk3", "pk33", null, "user3");
    ArgumentCaptor<byte[]> sentPayload = ArgumentCaptor.forClass(byte[].class);

    when(alertService.findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_ZONED_TIME))
        .thenReturn(List.of(alert1, alert2, alert3, alert4));
    when(weatherService.findForecastByLocation("Lyon", 3, "fr"))
        .thenReturn(
            WeatherUtils.buildForecastDto(
                "Lyon",
                List.of(
                    WeatherUtils.buildForecastDayDto(
                        "2022-10-22",
                        "Lyon",
                        List.of(
                            WeatherUtils.buildHourDto(
                                "2022-10-22 10:00",
                                ZonedDateTime.of(2022, 10, 22, 10, 0, 0, 0, ZoneId.of(TZ)),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                25D,
                                null,
                                null,
                                null,
                                null,
                                null))),
                    WeatherUtils.buildForecastDayDto(
                        "2022-10-23",
                        "Lyon",
                        List.of(
                            WeatherUtils.buildHourDto(
                                "2022-10-23 10:00",
                                ZonedDateTime.of(2022, 10, 23, 10, 0, 0, 0, ZoneId.of(TZ)),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                30,
                                null,
                                null,
                                null))))));
    when(weatherService.findForecastByLocation("Paris", 3, "fr"))
        .thenReturn(
            WeatherUtils.buildForecastDto(
                "Paris",
                List.of(
                    WeatherUtils.buildForecastDayDto(
                        "2022-10-22",
                        "Paris",
                        List.of(
                            WeatherUtils.buildHourDto(
                                "2022-10-22 14:00",
                                ZonedDateTime.of(2022, 10, 22, 14, 0, 0, 0, ZoneId.of(TZ)),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                25D,
                                null,
                                70,
                                null,
                                null,
                                null))))));
    when(subscriptionService.findAllByUsers(Set.of("user1", "user2", "user3")))
        .thenReturn(List.of(sub1, sub2, sub3));
    when(notificationService.send(any(), any())).thenReturn(List.of(HttpStatus.OK));

    alertScheduler.schedule();

    verify(alertService).findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_ZONED_TIME);
    verify(weatherService).findForecastByLocation("Lyon", 3, "fr");
    verify(weatherService).findForecastByLocation("Paris", 3, "fr");
    verify(subscriptionService).findAllByUsers(Set.of("user1", "user2", "user3"));
    verify(notificationService, times(3)).send(sentSubscriptions.capture(), sentPayload.capture());

    List<String> payloads =
        sentPayload.getAllValues().stream()
            .map(p -> new String(p, StandardCharsets.UTF_8))
            .sorted()
            .collect(Collectors.toList());

    assertAll(
        () -> assertEquals(3, payloads.size()),
        () -> assertEquals(String.format(PAYLOAD, "2022-10-22", "Lyon", 1L), payloads.get(0)),
        () -> assertEquals(String.format(PAYLOAD, "2022-10-22", "Lyon", 3L), payloads.get(1)),
        () -> assertEquals(String.format(PAYLOAD, "2022-10-22", "Paris", 4L), payloads.get(2)));

    List<SubscriptionDto> subs =
        sentSubscriptions.getAllValues().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
    subs.sort(Comparator.comparing(SubscriptionDto::getUserAgent));
    assertAll(
        () -> assertEquals(3, subs.size()),
        () -> assertThat(sub1).isEqualTo(subs.get(0)),
        () -> assertThat(sub2).isEqualTo(subs.get(1)),
        () -> assertThat(sub3).isEqualTo(subs.get(2)));
  }
}
