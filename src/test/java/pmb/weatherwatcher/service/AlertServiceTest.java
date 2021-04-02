package pmb.weatherwatcher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pmb.weatherwatcher.AlertUtils;
import pmb.weatherwatcher.dto.alert.AlertDto;
import pmb.weatherwatcher.exception.BadRequestException;
import pmb.weatherwatcher.mapper.AlertMapperImpl;
import pmb.weatherwatcher.mapper.MonitoredFieldMapperImpl;
import pmb.weatherwatcher.model.Alert;
import pmb.weatherwatcher.model.User;
import pmb.weatherwatcher.model.WeatherField;
import pmb.weatherwatcher.repository.AlertRepository;

@ActiveProfiles("test")
@Import({ AlertService.class, AlertMapperImpl.class, MonitoredFieldMapperImpl.class })
@ExtendWith(SpringExtension.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class AlertServiceTest {

    @MockBean
    private AlertRepository alertRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private AlertService alertService;
    private static AlertDto DUMMY_ALERT;

    @BeforeEach
    void tearUp() {
        DUMMY_ALERT = AlertUtils.buildAlertDto(null, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), AlertUtils.buildMonitoredDaysDto(true, false, true),
                List.of(OffsetTime.now()), List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)), "lyon", null);
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
            when(userService.getCurrentUser()).thenReturn(new User("test", "sfdg", "Lyon"));

            AlertDto result = alertService.save(DUMMY_ALERT);

            verify(alertRepository).save(captureSaved.capture());
            verify(userService).getCurrentUser();

            Alert saved = captureSaved.getValue();
            assertAll(() -> assertThat(DUMMY_ALERT).usingRecursiveComparison().as("result").isEqualTo(result), () -> assertNull(saved.getId(), "id"),
                    () -> assertEquals(DayOfWeek.MONDAY, saved.getTriggerDays().iterator().next(), "triggerDays"),
                    () -> assertTrue(saved.getMonitoredDays().getSameDay(), "sameDay"),
                    () -> assertFalse(saved.getMonitoredDays().getNextDay(), "nextDay"),
                    () -> assertTrue(saved.getMonitoredDays().getTwoDayLater(), "twoDay"),
                    () -> assertEquals(DUMMY_ALERT.getTriggerHour(), saved.getTriggerHour(), "triggerHour"),
                    () -> assertEquals(DUMMY_ALERT.getMonitoredHours().get(0), saved.getMonitoredHours().get(0), "monitoredHour"),
                    () -> assertEquals("lyon", saved.getLocation(), "location"), () -> assertNull(saved.getForceNotification(), "force"),
                    () -> assertNull(saved.getMonitoredFields().get(0).getId(), "fieldId"),
                    () -> assertEquals(WeatherField.FEELS_LIKE, saved.getMonitoredFields().get(0).getField(), "field"),
                    () -> assertEquals(35, saved.getMonitoredFields().get(0).getMax(), "fieldMax"),
                    () -> assertEquals(10, saved.getMonitoredFields().get(0).getMin(), "fieldMin"),
                    () -> assertEquals("test", saved.getUser().getLogin()), () -> assertEquals("Lyon", saved.getUser().getFavouriteLocation()),
                    () -> assertEquals("sfdg", saved.getUser().getPassword()));
        }

        @ParameterizedTest(name = "Given invalid alert #{index} then bad request exception")
        @MethodSource("pmb.weatherwatcher.service.AlertServiceTest#invalidAlertProvider")
        void given_invalid_alert_then_bad_request(AlertDto alert, String errorMsg) {
            assertThrows(BadRequestException.class, () -> alertService.save(alert), errorMsg);

            verify(alertRepository, never()).save(any());
            verify(userService, never()).getCurrentUser();
        }

    }

    static Stream<Arguments> invalidAlertProvider() {
        return Stream.of(
                Arguments.of(
                        AlertUtils.buildAlertDto(null, Set.of(DayOfWeek.MONDAY), OffsetTime.now(),
                                AlertUtils.buildMonitoredDaysDto(true, false, true), List.of(OffsetTime.now()),
                                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, null, null)), "lyon", null),
                        "Monitored field 'FEELS_LIKE' has its min and max values undefined"),
                Arguments.of(
                        AlertUtils.buildAlertDto(null, Set.of(DayOfWeek.MONDAY), OffsetTime.now(),
                                AlertUtils.buildMonitoredDaysDto(true, false, true), List.of(OffsetTime.now()),
                                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 35, 10)), "lyon", null),
                        "Monitored field 'FEELS_LIKE' has its min value greater than its max value: '[35, 10]'"),
                Arguments.of(
                        AlertUtils.buildAlertDto(null, Set.of(DayOfWeek.MONDAY), OffsetTime.now(),
                                AlertUtils.buildMonitoredDaysDto(null, false, null), List.of(OffsetTime.now()),
                                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 2, 10)), "lyon", null),
                        "Given alert has no monitored days"));
    }

}
