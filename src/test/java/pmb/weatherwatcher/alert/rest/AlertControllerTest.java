package pmb.weatherwatcher.alert.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pmb.weatherwatcher.TestUtils;
import pmb.weatherwatcher.alert.AlertUtils;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredDaysDto;
import pmb.weatherwatcher.alert.model.WeatherField;
import pmb.weatherwatcher.alert.service.AlertService;
import pmb.weatherwatcher.common.exception.BadRequestException;
import pmb.weatherwatcher.user.security.JwtTokenProvider;
import pmb.weatherwatcher.user.security.MyUserDetailsService;

@ActiveProfiles("test")
@Import({JwtTokenProvider.class})
@WebMvcTest(controllers = AlertController.class)
@MockBean(MyUserDetailsService.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class AlertControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private AlertService alertService;

  private static final AlertDto DUMMY_ALERT =
      AlertUtils.buildAlertDto(
          null,
          Set.of(DayOfWeek.MONDAY),
          OffsetTime.now(),
          new MonitoredDaysDto(),
          Set.of(OffsetTime.now()),
          List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, null, null)),
          "lyon",
          null);

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(alertService);
  }

  @Nested
  class Post {

    @Test
    void when_not_logged_then_unauthorized() throws Exception {
      mockMvc
          .perform(
              post("/alerts")
                  .content(objectMapper.writeValueAsString(DUMMY_ALERT))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$").doesNotExist());

      verify(alertService, never()).create(any());
    }

    @WithMockUser
    @ParameterizedTest(name = "Given invalid alert #{index} when saving then bad request")
    @MethodSource("pmb.weatherwatcher.alert.rest.AlertControllerTest#invalidAlertProvider")
    void when_failed_validation_then_bad_request(AlertDto alert, String field) throws Exception {
      mockMvc
          .perform(
              post("/alerts")
                  .content(objectMapper.writeValueAsString(alert))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$").value(Matchers.not(Matchers.matchesPattern(".*with \\d+ errors.*"))))
          .andExpect(
              jsonPath("$")
                  .value(
                      Matchers.containsString(
                          "Field error in object 'alertDto' on field '" + field)));

      verify(alertService, never()).create(any());
    }

    @Test
    @WithMockUser
    void ok() throws Exception {
      AlertDto alert =
          AlertUtils.buildAlertDto(
              9L,
              Set.of(DayOfWeek.MONDAY),
              OffsetTime.now(),
              new MonitoredDaysDto(),
              Set.of(OffsetTime.now()),
              List.of(AlertUtils.buildMonitoredFieldDto(5L, WeatherField.FEELS_LIKE, 62, 12)),
              "lyon",
              false);

      when(alertService.create(any())).thenAnswer(a -> a.getArgument(0));

      assertThat(alert)
          .usingRecursiveComparison()
          .isEqualTo(
              objectMapper.readValue(
                  TestUtils.readResponse.apply(
                      mockMvc
                          .perform(
                              post("/alerts")
                                  .content(objectMapper.writeValueAsString(alert))
                                  .contentType(MediaType.APPLICATION_JSON_VALUE))
                          .andExpect(status().isOk())),
                  AlertDto.class));

      verify(alertService).create(any());
    }

    @Test
    @WithMockUser
    void given_service_throw_bad_req_exception_then_400() throws Exception {
      AlertDto alert =
          AlertUtils.buildAlertDto(
              9L,
              Set.of(DayOfWeek.MONDAY),
              OffsetTime.now(),
              new MonitoredDaysDto(),
              Set.of(OffsetTime.now()),
              List.of(AlertUtils.buildMonitoredFieldDto(5L, WeatherField.FEELS_LIKE, 62, 12)),
              "lyon",
              true);

      when(alertService.create(any())).thenThrow(BadRequestException.class);

      mockMvc
          .perform(
              post("/alerts")
                  .content(objectMapper.writeValueAsString(alert))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(alertService).create(any());
    }
  }

  @Nested
  class GetAllByUser {

    @Test
    void when_not_logged_then_unauthorized() throws Exception {
      mockMvc
          .perform(get("/alerts").contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$").doesNotExist());

      verify(alertService, never()).findAllForCurrentUser();
    }

    @Test
    @WithMockUser
    void ok() throws Exception {
      when(alertService.findAllForCurrentUser()).thenReturn(List.of(DUMMY_ALERT));

      assertThat(DUMMY_ALERT)
          .usingRecursiveComparison()
          .isEqualTo(
              objectMapper
                  .readValue(
                      TestUtils.readResponse.apply(
                          mockMvc
                              .perform(get("/alerts").contentType(MediaType.APPLICATION_JSON_VALUE))
                              .andExpect(status().isOk())),
                      AlertDto[].class)[0]);

      verify(alertService).findAllForCurrentUser();
    }
  }

  @Nested
  class Put {

    @Test
    void when_not_logged_then_unauthorized() throws Exception {
      mockMvc
          .perform(
              put("/alerts")
                  .content(objectMapper.writeValueAsString(DUMMY_ALERT))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$").doesNotExist());

      verify(alertService, never()).update(any());
    }

    @Test
    @WithMockUser
    void ok() throws Exception {
      when(alertService.update(any())).thenAnswer(a -> a.getArgument(0));

      assertThat(DUMMY_ALERT)
          .usingRecursiveComparison()
          .isEqualTo(
              objectMapper.readValue(
                  TestUtils.readResponse.apply(
                      mockMvc
                          .perform(
                              put("/alerts")
                                  .content(objectMapper.writeValueAsString(DUMMY_ALERT))
                                  .contentType(MediaType.APPLICATION_JSON_VALUE))
                          .andExpect(status().isOk())),
                  AlertDto.class));

      verify(alertService).update(any());
    }
  }

  @Nested
  class Delete {

    @Captor ArgumentCaptor<List<Long>> captorIds;

    @Test
    void when_not_logged_then_unauthorized() throws Exception {
      mockMvc
          .perform(
              delete("/alerts").param("ids", "2,3").contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$").doesNotExist());

      verify(alertService, never()).delete(any());
    }

    @Test
    @WithMockUser
    void missing_ids_then_bad_request() throws Exception {
      mockMvc
          .perform(delete("/alerts").contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(alertService, never()).delete(any());
    }

    @Test
    @WithMockUser
    void invalid_id_given_then_bad_request() throws Exception {
      mockMvc
          .perform(
              delete("/alerts").param("ids", "2.3").contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(alertService, never()).delete(any());
    }

    @Test
    @WithMockUser
    void ok() throws Exception {
      doNothing().when(alertService).delete(List.of(2L, 3L));

      mockMvc
          .perform(
              delete("/alerts").param("ids", "2,3").contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isOk());

      verify(alertService).delete(captorIds.capture());

      List<Long> captured = captorIds.getValue();
      assertAll(
          () -> assertEquals(2, captured.size()),
          () -> assertEquals(2L, captured.get(0)),
          () -> assertEquals(3L, captured.get(1)));
    }
  }

  static Stream<Arguments> invalidAlertProvider() {
    return Stream.of(
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                null,
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)),
                "lyon",
                true),
            "triggerDays"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Collections.emptySet(),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)),
                "lyon",
                true),
            "triggerDays"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                null,
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)),
                "lyon",
                true),
            "triggerHour"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                null,
                Set.of(OffsetTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)),
                "lyon",
                true),
            "monitoredDays"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                null,
                List.of(AlertUtils.buildMonitoredFieldDto(9L, WeatherField.FEELS_LIKE, 10, 35)),
                "lyon",
                true),
            "monitoredHours"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Collections.emptySet(),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.FEELS_LIKE, 10, 35)),
                "lyon",
                true),
            "monitoredHours"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                null,
                "lyon",
                true),
            "monitoredFields"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                Collections.emptyList(),
                "lyon",
                true),
            "monitoredFields"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, null, 10, 35)),
                "lyon",
                true),
            "monitoredFields"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.HUMIDITY, 10, 35)),
                null,
                true),
            "location"),
        arguments(
            AlertUtils.buildAlertDto(
                5L,
                Set.of(DayOfWeek.MONDAY),
                OffsetTime.now(),
                new MonitoredDaysDto(),
                Set.of(OffsetTime.now()),
                List.of(AlertUtils.buildMonitoredFieldDto(null, WeatherField.HUMIDITY, 10, 35)),
                "  ",
                true),
            "location"));
  }
}
