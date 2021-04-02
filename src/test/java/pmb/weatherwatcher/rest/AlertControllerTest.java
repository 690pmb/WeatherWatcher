package pmb.weatherwatcher.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import pmb.weatherwatcher.AlertUtils;
import pmb.weatherwatcher.TestUtils;
import pmb.weatherwatcher.dto.alert.AlertDto;
import pmb.weatherwatcher.dto.alert.MonitoredDaysDto;
import pmb.weatherwatcher.exception.BadRequestException;
import pmb.weatherwatcher.model.WeatherField;
import pmb.weatherwatcher.security.JwtTokenProvider;
import pmb.weatherwatcher.security.MyUserDetailsService;
import pmb.weatherwatcher.service.AlertService;

@ActiveProfiles("test")
@Import({ JwtTokenProvider.class })
@WebMvcTest(controllers = AlertController.class)
@MockBean(MyUserDetailsService.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AlertService alertService;

    private static final AlertDto DUMMY_ALERT = AlertUtils.buildAlertDto(null, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(),
            List.of(OffsetTime.now()), List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, null, null)), "lyon", null);

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(alertService);
    }

    @Nested
    class Post {

        @Test
        void when_not_logged_then_unauthorized() throws Exception {
            mockMvc.perform(post("/alerts").content(objectMapper.writeValueAsString(DUMMY_ALERT)).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnauthorized()).andExpect(jsonPath("$").doesNotExist());

            verify(alertService, never()).save(any());
        }

        @WithMockUser
        @ParameterizedTest(name = "Given invalid alert #{index} when saving then bad request")
        @MethodSource("pmb.weatherwatcher.rest.AlertControllerTest#invalidAlertProvider")
        void when_failed_validation_then_bad_request(AlertDto alert, String field) throws Exception {
            mockMvc.perform(post("/alerts").content(objectMapper.writeValueAsString(alert)).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$").value(Matchers.not(Matchers.matchesPattern(".*with \\d+ errors.*"))))
                    .andExpect(jsonPath("$").value(Matchers.containsString("Field error in object 'alertDto' on field '" + field)));

            verify(alertService, never()).save(any());
        }

        @Test
        @WithMockUser
        void ok() throws Exception {
            AlertDto alert = AlertUtils.buildAlertDto(9L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(),
                    List.of(OffsetTime.now()), List.of(AlertUtils.buildMonitoredFieldDto(5L, WeatherField.FEELS_LIKE, 62, 12)), "lyon", false);

            when(alertService.save(any())).thenAnswer(a -> a.getArgument(0));

            assertThat(alert).usingRecursiveComparison()
                    .isEqualTo(objectMapper.readValue(TestUtils.readResponse.apply(mockMvc
                            .perform(post("/alerts").content(objectMapper.writeValueAsString(alert)).contentType(MediaType.APPLICATION_JSON_VALUE))
                            .andExpect(status().isOk())), AlertDto.class));

            verify(alertService).save(any());
        }

        @Test
        @WithMockUser
        void given_service_throw_bad_req_exception_then_400() throws Exception {
            AlertDto alert = AlertUtils.buildAlertDto(9L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(),
                    List.of(OffsetTime.now()), List.of(AlertUtils.buildMonitoredFieldDto(5L, WeatherField.FEELS_LIKE, 62, 12)), "lyon", true);

            when(alertService.save(any())).thenThrow(BadRequestException.class);

            mockMvc.perform(post("/alerts").content(objectMapper.writeValueAsString(alert)).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadRequest());

            verify(alertService).save(any());
        }

    }

    static Stream<Arguments> invalidAlertProvider() {
        return Stream.of(
                arguments(AlertUtils.buildAlertDto(5L, null, OffsetTime.now(), new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)), "lyon", true), "triggerDays"),
                arguments(AlertUtils.buildAlertDto(5L, Collections.emptySet(), OffsetTime.now(), new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)), "lyon", true), "triggerDays"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), null, new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)), "lyon", true), "triggerHour"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), null, List.of(OffsetTime.now()),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)), "lyon", true), "monitoredDays"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(), null,
                        List.of(AlertUtils.buildMonitoredFieldDto(9L, WeatherField.FEELS_LIKE, 10, 35)), "lyon", true), "monitoredHours"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(), Collections.emptyList(),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)), "lyon", true), "monitoredHours"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        null, "lyon", true), "monitoredFields"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        Collections.emptyList(), "lyon", true), "monitoredFields"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, null, 10, 35)), "lyon", true), "monitoredFields"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.HUMIDITY, 10, 35)), null, true), "location"),
                arguments(AlertUtils.buildAlertDto(5L, Set.of(DayOfWeek.MONDAY), OffsetTime.now(), new MonitoredDaysDto(), List.of(OffsetTime.now()),
                        List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.HUMIDITY, 10, 35)), "  ", true), "location"));
    }

}
