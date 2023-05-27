package pmb.weatherwatcher.notification.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pmb.weatherwatcher.notification.NotificationUtils.buildSubscriptionDto;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pmb.weatherwatcher.TestUtils;
import pmb.weatherwatcher.notification.NotificationUtils;
import pmb.weatherwatcher.notification.dto.DeleteSubscriptionDto;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.service.SubscriptionService;
import pmb.weatherwatcher.user.security.JwtTokenProvider;
import pmb.weatherwatcher.user.security.MyUserDetailsService;

@ActiveProfiles("test")
@Import({JwtTokenProvider.class})
@WebMvcTest(controllers = SubscriptionController.class)
@MockBean(MyUserDetailsService.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class SubscriptionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private SubscriptionService subscriptionService;

  private static final SubscriptionDto VALID_SUBSCRIPTION =
      NotificationUtils.buildSubscriptionDto(
          "USERAGENT", "ENDPOINT", "PUBLIC", "PRIVATE", 6L, null);
  private static final String USER_AGENT = "userAgent";
  private static final String DELETE_SUBSCRIPTION = "{\"userAgent\": \"" + USER_AGENT + "\"}";

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(subscriptionService);
  }

  @Nested
  class Post {

    @Test
    void when_not_logged_then_unauthorized() throws Exception {
      mockMvc
          .perform(
              post("/notifications/subscriptions")
                  .content(objectMapper.writeValueAsString(VALID_SUBSCRIPTION))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$").doesNotExist());

      verify(subscriptionService, never()).save(any());
    }

    @WithMockUser
    @ParameterizedTest(name = "Given invalid subscription #{index} when saving then bad request")
    @MethodSource(
        "pmb.weatherwatcher.notification.rest.SubscriptionControllerTest#invalidSubscriptionProvider")
    void when_failed_validation_then_bad_request(SubscriptionDto sub) throws Exception {
      mockMvc
          .perform(
              post("/notifications/subscriptions")
                  .content(objectMapper.writeValueAsString(sub))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$").value(Matchers.matchesPattern(".*Validation failed.*")));

      verify(subscriptionService, never()).save(any());
    }

    @Test
    @WithMockUser
    void ok() throws Exception {
      when(subscriptionService.save(any())).thenAnswer(a -> a.getArgument(0));

      assertThat(VALID_SUBSCRIPTION)
          .usingRecursiveComparison()
          .isEqualTo(
              objectMapper.readValue(
                  TestUtils.readResponse.apply(
                      mockMvc
                          .perform(
                              post("/notifications/subscriptions")
                                  .content(objectMapper.writeValueAsString(VALID_SUBSCRIPTION))
                                  .contentType(MediaType.APPLICATION_JSON_VALUE))
                          .andExpect(status().isOk())),
                  SubscriptionDto.class));

      verify(subscriptionService).save(any());
    }
  }

  @Nested
  class Delete {

    @Test
    void when_not_logged_then_unauthorized() throws Exception {
      mockMvc
          .perform(
              delete("/notifications/subscriptions")
                  .content(DELETE_SUBSCRIPTION)
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized());

      verify(subscriptionService, never()).deleteOthersByUserId(any());
    }

    @Test
    @WithMockUser
    void ok() throws Exception {
      doNothing().when(subscriptionService).deleteOthersByUserId(USER_AGENT);

      mockMvc
          .perform(
              delete("/notifications/subscriptions")
                  .content(DELETE_SUBSCRIPTION)
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isNoContent());

      verify(subscriptionService).deleteOthersByUserId(USER_AGENT);
    }

    @Test
    @WithMockUser
    void given_service_throws_not_found_then_unauthorized() throws Exception {

      doThrow(UsernameNotFoundException.class)
          .when(subscriptionService)
          .deleteOthersByUserId(USER_AGENT);

      mockMvc
          .perform(
              delete("/notifications/subscriptions")
                  .content(DELETE_SUBSCRIPTION)
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized());

      verify(subscriptionService).deleteOthersByUserId(USER_AGENT);
    }

    @ParameterizedTest
    @WithMockUser
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void given_invalid_user_agent_when_deleting_then_bad_request(String userAgent)
        throws Exception {
      DeleteSubscriptionDto dto = new DeleteSubscriptionDto();
      dto.setUserAgent(userAgent);

      mockMvc
          .perform(
              delete("/notifications/subscriptions")
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(subscriptionService, never()).deleteOthersByUserId(any());
    }
  }

  static Stream<Arguments> invalidSubscriptionProvider() {
    return Stream.of(
        arguments(buildSubscriptionDto(null, null, null, null, 8L, null)),
        arguments(buildSubscriptionDto(null, null, null, "private", null, null)),
        arguments(buildSubscriptionDto(null, null, "public", null, null, null)),
        arguments(buildSubscriptionDto(null, null, "public", "private", null, null)),
        arguments(buildSubscriptionDto(null, "endpoint", null, null, null, null)),
        arguments(buildSubscriptionDto(null, "endpoint", null, "private", null, null)),
        arguments(buildSubscriptionDto(null, "endpoint", "public", null, null, null)),
        arguments(buildSubscriptionDto(null, "endpoint", "public", "private", null, null)),
        arguments(buildSubscriptionDto("userAgent", null, null, null, 8L, null)),
        arguments(buildSubscriptionDto("userAgent", null, null, "private", null, null)),
        arguments(buildSubscriptionDto("userAgent", null, "public", null, null, null)),
        arguments(buildSubscriptionDto("userAgent", null, "public", "private", null, null)),
        arguments(buildSubscriptionDto("userAgent", "endpoint", null, null, null, null)),
        arguments(buildSubscriptionDto("userAgent", "endpoint", null, "private", null, null)),
        arguments(buildSubscriptionDto("userAgent", "endpoint", "public", "private", 8L, "user")),
        arguments(buildSubscriptionDto("userAgent", "endpoint", "public", null, null, null)));
  }
}
