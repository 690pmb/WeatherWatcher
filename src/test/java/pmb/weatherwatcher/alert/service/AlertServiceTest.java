package pmb.weatherwatcher.alert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.util.CollectionUtils;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.alert.AlertUtils;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.mapper.AlertMapperImpl;
import pmb.weatherwatcher.alert.mapper.MonitoredFieldMapperImpl;
import pmb.weatherwatcher.alert.model.Alert;
import pmb.weatherwatcher.alert.model.WeatherField;
import pmb.weatherwatcher.alert.repository.AlertRepository;
import pmb.weatherwatcher.common.exception.BadRequestException;
import pmb.weatherwatcher.common.exception.NotFoundException;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.service.UserService;

@ServiceTestRunner
@Import({AlertService.class, AlertMapperImpl.class, MonitoredFieldMapperImpl.class})
class AlertServiceTest {

  @MockBean private AlertRepository alertRepository;
  @MockBean private UserService userService;
  @Autowired private AlertService alertService;
  private static AlertDto DUMMY_ALERT;
  private static User DUMMY_USER =
      new User("test", "sfdg", "Lyon", Language.FRENCH, "Europe/Berlin");
  private static final LocalTime time = LocalTime.of(11, 25, 0, 0);

  @BeforeEach
  void tearUp() {
    DUMMY_ALERT =
        AlertUtils.buildAlertDto(
            null,
            Set.of(DayOfWeek.MONDAY),
            time,
            AlertUtils.buildMonitoredDaysDto(true, false, true),
            Set.of(time),
            List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)),
            "lyon",
            false,
            "user",
            null);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(alertRepository, userService);
  }

  @Nested
  class Save {

    @Test
    void ok() {
      ArgumentCaptor<Alert> captureSaved = ArgumentCaptor.forClass(Alert.class);

      when(alertRepository.save(any())).thenAnswer(a -> a.getArgument(0));
      when(userService.getCurrentUser()).thenReturn(DUMMY_USER);

      AlertDto result = alertService.create(DUMMY_ALERT);

      verify(alertRepository).save(captureSaved.capture());
      verify(userService).getCurrentUser();

      Alert saved = captureSaved.getValue();
      assertAll(
          () ->
              assertThat(DUMMY_ALERT)
                  .usingRecursiveComparison(
                      RecursiveComparisonConfiguration.builder()
                          .withIgnoredFields("triggerHour", "monitoredHours", "user")
                          .withIgnoreCollectionOrder(true)
                          .build())
                  .as("result")
                  .isEqualTo(result),
          () -> assertNull(saved.getId(), "id"),
          () ->
              assertEquals(
                  DayOfWeek.MONDAY, saved.getTriggerDays().iterator().next(), "triggerDays"),
          () -> assertTrue(saved.getMonitoredDays().getSameDay(), "sameDay"),
          () -> assertFalse(saved.getMonitoredDays().getNextDay(), "nextDay"),
          () -> assertTrue(saved.getMonitoredDays().getTwoDayLater(), "twoDay"),
          () -> assertEquals(DUMMY_ALERT.getTriggerHour(), saved.getTriggerHour(), "triggerHour"),
          () ->
              assertEquals(
                  DUMMY_ALERT.getMonitoredHours().iterator().next(),
                  saved.getMonitoredHours().iterator().next(),
                  "monitoredHour"),
          () -> assertEquals("lyon", saved.getLocation(), "location"),
          () -> assertFalse(saved.getForceNotification(), "force"),
          () -> assertNull(saved.getMonitoredFields().get(0).getId(), "fieldId"),
          () ->
              assertEquals(
                  WeatherField.FEELS_LIKE, saved.getMonitoredFields().get(0).getField(), "field"),
          () -> assertEquals(35, saved.getMonitoredFields().get(0).getMax(), "fieldMax"),
          () -> assertEquals(10, saved.getMonitoredFields().get(0).getMin(), "fieldMin"),
          () -> assertEquals("test", saved.getUser().getLogin()),
          () -> assertEquals("Lyon", saved.getUser().getFavouriteLocation()),
          () -> assertEquals("sfdg", saved.getUser().getPassword()),
          () -> assertEquals("Europe/Berlin", saved.getUser().getTimezone()));
    }

    @ParameterizedTest(name = "Given invalid alert #{index} then bad request exception")
    @MethodSource("pmb.weatherwatcher.alert.service.AlertServiceTest#invalidAlertProvider")
    void given_invalid_alert_then_bad_request(AlertDto alert, String errorMsg) {
      assertThrows(BadRequestException.class, () -> alertService.create(alert), errorMsg);

      verify(alertRepository, never()).save(any());
      verify(userService, never()).getCurrentUser();
    }
  }

  @Test
  void findAllForCurrentUser() {
    Pageable pageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "location"));
    Alert a1 = new Alert();
    a1.setId(1L);
    Alert a2 = new Alert();
    a2.setId(2L);

    when(userService.getCurrentUser()).thenReturn(DUMMY_USER);
    when(alertRepository.findDistinctByUserLogin("test", pageable))
        .thenReturn(new PageImpl<>(List.of(a1, a2)));

    Page<AlertDto> result = alertService.findAllForCurrentUser(pageable);

    List<AlertDto> content = result.getContent();
    assertAll(
        () -> assertFalse(CollectionUtils.isEmpty(content)),
        () -> assertEquals(2, content.size()),
        () -> assertEquals(1L, content.get(0).getId()),
        () -> assertEquals(2L, content.get(1).getId()));

    verify(userService).getCurrentUser();
    verify(alertRepository).findDistinctByUserLogin("test", pageable);
  }

  @Test
  void findAllToTrigger() {
    Alert a1 = new Alert();
    a1.setId(1L);
    a1.setTriggerHour(LocalTime.of(15, 12, 47));
    a1.setUser(new User("a1", null, null, null, "Europe/Paris"));
    Alert a2 = new Alert();
    a2.setId(2L);
    a2.setTriggerHour(LocalTime.of(14, 12, 47));
    a2.setUser(new User("a2", null, null, null, "Europe/Paris"));

    ZonedDateTime triggerHour =
        ZonedDateTime.of(LocalDate.now(), LocalTime.of(14, 12, 47, 195), ZoneId.of("Z"));

    when(alertRepository.findAllByTriggerDays(DayOfWeek.FRIDAY)).thenReturn(List.of(a1, a2));

    List<AlertDto> trigger = alertService.findAllToTrigger(DayOfWeek.FRIDAY, triggerHour);

    assertAll(
        () -> assertEquals(1, trigger.size()),
        () -> assertEquals(1, trigger.get(0).getId()),
        () -> assertEquals("a1", trigger.get(0).getUser()),
        () -> assertEquals("Europe/Paris", trigger.get(0).getTimezone()));

    verify(alertRepository).findAllByTriggerDays(DayOfWeek.FRIDAY);
  }

  @Nested
  class Update {

    @Test
    void ok() {
      DUMMY_ALERT.setId(5L);
      DUMMY_ALERT.getMonitoredFields().get(0).setMax(null);

      when(userService.getCurrentUser()).thenReturn(DUMMY_USER);
      when(alertRepository.findByIdAndUserLogin(5L, "test")).thenReturn(Optional.of(new Alert()));
      when(alertRepository.save(any())).thenAnswer(a -> a.getArgument(0));

      AlertDto result = alertService.update(DUMMY_ALERT);

      assertThat(DUMMY_ALERT)
          .usingRecursiveComparison(
              RecursiveComparisonConfiguration.builder()
                  .withIgnoredFields("triggerHour", "monitoredHours", "user")
                  .withIgnoreCollectionOrder(true)
                  .build())
          .as("result")
          .isEqualTo(result);

      verify(userService).getCurrentUser();
      verify(alertRepository).findByIdAndUserLogin(5L, "test");
      verify(alertRepository).save(any());
    }

    @Test
    void id_null_then_bad_request() {
      assertThrows(
          BadRequestException.class,
          () -> alertService.update(DUMMY_ALERT),
          "Alert to update with id 'null' is unknown");

      verify(alertRepository, never()).findById(anyLong());
      verify(userService, never()).getCurrentUser();
      verify(alertRepository, never()).save(any());
    }

    @Test
    void alert_not_found_then_bad_request() {
      DUMMY_ALERT.setId(5L);
      when(userService.getCurrentUser()).thenReturn(DUMMY_USER);
      when(alertRepository.findByIdAndUserLogin(5L, "test")).thenReturn(Optional.empty());

      assertThrows(
          BadRequestException.class,
          () -> alertService.update(DUMMY_ALERT),
          "Alert to update with id '5' is unknown");

      verify(userService).getCurrentUser();
      verify(alertRepository).findByIdAndUserLogin(5L, "test");
      verify(alertRepository, never()).save(any());
    }
  }

  @Nested
  class FindById {

    @Test
    void when_alert_not_exist_then_not_found_exception() {
      when(userService.getCurrentUser()).thenReturn(DUMMY_USER);
      when(alertRepository.findByIdAndUserLogin(5L, "test")).thenReturn(Optional.empty());

      assertThrows(
          NotFoundException.class,
          () -> alertService.findById(5L),
          "Alert with id: '5' was not found");

      verify(userService).getCurrentUser();
      verify(alertRepository).findByIdAndUserLogin(5L, "test");
    }

    @Test
    void ok() {
      Alert alert = new Alert();
      alert.setId(56L);

      when(userService.getCurrentUser()).thenReturn(DUMMY_USER);
      when(alertRepository.findByIdAndUserLogin(5L, "test")).thenReturn(Optional.of(alert));

      AlertDto actual = alertService.findById(5L);

      assertThat(actual.getId()).isEqualTo(alert.getId());

      verify(userService).getCurrentUser();
      verify(alertRepository).findByIdAndUserLogin(5L, "test");
    }
  }

  @Nested
  class Delete {

    @Test
    void alert_not_found_then_bad_request() {
      when(userService.getCurrentUser()).thenReturn(DUMMY_USER);
      when(alertRepository.findByIdAndUserLogin(5L, "test")).thenReturn(Optional.empty());

      assertThrows(
          BadRequestException.class,
          () -> alertService.delete(List.of(5L)),
          "Alert to delete with id '5' doesn't exist or doesn't belong to logged user");

      verify(userService).getCurrentUser();
      verify(alertRepository).findByIdAndUserLogin(5L, "test");
      verify(alertRepository, never()).delete(any());
    }

    @Test
    void ok() {
      Alert alert = new Alert();
      alert.setId(56L);
      ArgumentCaptor<Alert> deletedCaptor = ArgumentCaptor.forClass(Alert.class);

      when(userService.getCurrentUser()).thenReturn(DUMMY_USER);
      when(alertRepository.findByIdAndUserLogin(5L, "test")).thenReturn(Optional.of(alert));
      when(alertRepository.findByIdAndUserLogin(8L, "test")).thenReturn(Optional.of(alert));
      doNothing().when(alertRepository).delete(any());

      alertService.delete(List.of(5L, 8L));

      verify(userService, times(2)).getCurrentUser();
      verify(alertRepository).findByIdAndUserLogin(5L, "test");
      verify(alertRepository).findByIdAndUserLogin(8L, "test");
      verify(alertRepository, times(2)).delete(deletedCaptor.capture());

      assertEquals(56L, deletedCaptor.getValue().getId());
    }
  }

  static Stream<Arguments> invalidAlertProvider() {
    return Stream.of(
        Arguments.of(
            AlertUtils.buildAlertDto(
                null,
                Set.of(DayOfWeek.MONDAY),
                LocalTime.now(),
                AlertUtils.buildMonitoredDaysDto(true, false, true),
                Set.of(LocalTime.now()),
                List.of(
                    AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, null, null)),
                "lyon",
                null,
                "user",
                null),
            "Monitored field 'FEELS_LIKE' has its min and max values undefined"),
        Arguments.of(
            AlertUtils.buildAlertDto(
                null,
                Set.of(DayOfWeek.MONDAY),
                LocalTime.now(),
                AlertUtils.buildMonitoredDaysDto(true, false, true),
                Set.of(LocalTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 35, 10)),
                "lyon",
                true,
                "user",
                null),
            "Monitored field 'FEELS_LIKE' has its min value greater than its max value: '[35, 10]'"),
        Arguments.of(
            AlertUtils.buildAlertDto(
                null,
                Set.of(DayOfWeek.MONDAY),
                LocalTime.now(),
                AlertUtils.buildMonitoredDaysDto(null, false, null),
                Set.of(LocalTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 2, 10)),
                "lyon",
                null,
                "user",
                null),
            "Given alert has no monitored days"),
        Arguments.of(
            AlertUtils.buildAlertDto(
                6L,
                Set.of(DayOfWeek.MONDAY),
                LocalTime.now(),
                AlertUtils.buildMonitoredDaysDto(null, false, null),
                Set.of(LocalTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 2, 10)),
                "lyon",
                false,
                "user",
                null),
            "Ids must be null when creating an alert"),
        Arguments.of(
            AlertUtils.buildAlertDto(
                null,
                Set.of(DayOfWeek.MONDAY),
                LocalTime.now(),
                AlertUtils.buildMonitoredDaysDto(null, false, null),
                Set.of(LocalTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(3L, WeatherField.FEELS_LIKE, 2, 10)),
                "lyon",
                null,
                "user",
                null),
            "Ids must be null when creating an alert"));
  }
}
