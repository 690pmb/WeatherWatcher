package pmb.weatherwatcher.notification.rest;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import pmb.weatherwatcher.TestUtils;
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

  private static final SubscriptionDto SUB = new SubscriptionDto();

  @BeforeEach
  void tearUp() {
    SUB.setEndpoint("ENDPOINT");
    SUB.setUserAgent("USERAGENT");
    SUB.setExpirationTime(6L);
  }

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
                  .content(objectMapper.writeValueAsString(SUB))
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

      assertThat(SUB)
          .usingRecursiveComparison()
          .isEqualTo(
              objectMapper.readValue(
                  TestUtils.readResponse.apply(
                      mockMvc
                          .perform(
                              post("/notifications/subscriptions")
                                  .content(objectMapper.writeValueAsString(SUB))
                                  .contentType(MediaType.APPLICATION_JSON_VALUE))
                          .andExpect(status().isOk())),
                  SubscriptionDto.class));

      verify(subscriptionService).save(any());
    }
  }

  static Stream<Arguments> invalidSubscriptionProvider() {
    SubscriptionDto sub1 = new SubscriptionDto();
    sub1.setExpirationTime(8L);
    SubscriptionDto sub2 = new SubscriptionDto();
    sub2.setEndpoint("endpoint");
    SubscriptionDto sub3 = new SubscriptionDto();
    sub3.setUserAgent("useragent");
    return Stream.of(arguments(sub1), arguments(sub2), arguments(sub3));
  }
}
