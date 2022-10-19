package pmb.weatherwatcher.alert.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.TestUtils;
import pmb.weatherwatcher.alert.AlertUtils;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.model.WeatherField;
import pmb.weatherwatcher.alert.service.AlertService;
import pmb.weatherwatcher.notification.NotificationUtils;
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

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2022-10-22T10:00:00Z"), ZoneOffset.UTC);
  private static final LocalTime DUMMY_LOCAL_TIME = LocalTime.of(10, 0, 0);

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
    when(alertService.findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_LOCAL_TIME))
        .thenReturn(Collections.emptyList());

    alertScheduler.schedule();

    verify(alertService).findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_LOCAL_TIME);
    verify(weatherService, never()).findForecastbyLocation(any(), any(), any());
    verify(subscriptionService, never()).findAllByUsers(any());
    verify(notificationService, never()).send(any(), any());
  }

  @Test
  void given_alert_then_notification() {
    AlertDto alert1 =
        AlertUtils.buildAlertDto(
            1L,
            null,
            null,
            AlertUtils.buildMonitoredDaysDto(true, false, false),
            Set.of(TestUtils.buildOffsetTime(10, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 20)),
            "Lyon",
            false,
            "user1");
    AlertDto alert2 =
        AlertUtils.buildAlertDto(
            2L,
            null,
            null,
            AlertUtils.buildMonitoredDaysDto(true, true, true),
            Set.of(
                TestUtils.buildOffsetTime(10, 0),
                TestUtils.buildOffsetTime(20, 0),
                TestUtils.buildOffsetTime(17, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.CHANCE_OF_RAIN, 10, 60)),
            "Lyon",
            false,
            "user1");
    AlertDto alert3 =
        AlertUtils.buildAlertDto(
            3L,
            null,
            null,
            AlertUtils.buildMonitoredDaysDto(false, false, false),
            Set.of(TestUtils.buildOffsetTime(10, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 30)),
            "Lyon",
            true,
            "user2");
    AlertDto alert4 =
        AlertUtils.buildAlertDto(
            4L,
            null,
            null,
            AlertUtils.buildMonitoredDaysDto(true, false, true),
            Set.of(TestUtils.buildOffsetTime(14, 0), TestUtils.buildOffsetTime(17, 0)),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.CHANCE_OF_RAIN, 10, 60)),
            "Paris",
            false,
            "user3");

    when(alertService.findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_LOCAL_TIME))
        .thenReturn(List.of(alert1, alert2, alert3, alert4));
    when(weatherService.findForecastbyLocation("Lyon", 3, "fr"))
        .thenReturn(
            WeatherUtils.buildForecastDto(
                "Lyon",
                List.of(
                    WeatherUtils.buildForecastDayDto(
                        "2022-10-22",
                        List.of(
                            WeatherUtils.builHourDto(
                                "2022-10-22 10:00",
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
                        List.of(
                            WeatherUtils.builHourDto(
                                "2022-10-23 10:00",
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
    when(weatherService.findForecastbyLocation("Paris", 3, "fr"))
        .thenReturn(
            WeatherUtils.buildForecastDto(
                "Paris",
                List.of(
                    WeatherUtils.buildForecastDayDto(
                        "2022-10-22",
                        List.of(
                            WeatherUtils.builHourDto(
                                "2022-10-22 14:00",
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
        .thenReturn(
            List.of(
                NotificationUtils.buildSubscriptionDto("ua1", "end1", "pk1", "pk11", null, "user1"),
                NotificationUtils.buildSubscriptionDto("ua2", "end2", "pk2", "pk22", null, "user3"),
                NotificationUtils.buildSubscriptionDto(
                    "ua3", "end3", "pk3", "pk33", null, "user3")));
    when(notificationService.send(any(), any())).thenReturn(List.of(HttpStatus.OK));

    alertScheduler.schedule();

    verify(alertService).findAllToTrigger(DayOfWeek.SATURDAY, DUMMY_LOCAL_TIME);
    verify(weatherService).findForecastbyLocation("Lyon", 3, "fr");
    verify(weatherService).findForecastbyLocation("Paris", 3, "fr");
    verify(subscriptionService).findAllByUsers(Set.of("user1", "user2", "user3"));
    verify(notificationService).send(any(), any());
  }
}
