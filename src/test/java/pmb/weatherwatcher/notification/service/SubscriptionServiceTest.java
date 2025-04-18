package pmb.weatherwatcher.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.notification.NotificationUtils;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.mapper.SubscriptionMapperImpl;
import pmb.weatherwatcher.notification.model.Subscription;
import pmb.weatherwatcher.notification.model.SubscriptionId;
import pmb.weatherwatcher.notification.repository.SubscriptionRepository;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.security.JwtTokenProvider;
import pmb.weatherwatcher.user.service.UserService;

@ServiceTestRunner
@Import({SubscriptionService.class, SubscriptionMapperImpl.class})
class SubscriptionServiceTest {
  private static final String TZ = "Europe/Paris";

  @MockBean private SubscriptionRepository subscriptionRepository;
  @MockBean private UserService userService;
  @Autowired private SubscriptionService subscriptionService;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(subscriptionRepository, userService);
  }

  @Nested
  class save {

    @Test
    void update_existing() {
      ArgumentCaptor<Subscription> captureSaved = ArgumentCaptor.forClass(Subscription.class);
      String userAgent = "userAgent";
      SubscriptionDto toSave =
          NotificationUtils.buildSubscriptionDto(
              userAgent, "end", "public2", "private2", 56L, null);
      Subscription existing = new Subscription();
      existing.setEndpoint("point");
      existing.setExpirationTime(98L);
      existing.setId(new SubscriptionId("userAgent2", "login2"));
      existing.setUser(new User("login2", "mdp", "Paris", Language.FRENCH, TZ));
      existing.setPublicKey("public");
      existing.setPrivateKey("private");

      when(userService.getCurrentUser())
          .thenReturn(new User("login", "pwd", "Lyon", Language.FRENCH, TZ));
      when(subscriptionRepository.findById(new SubscriptionId(userAgent, "login")))
          .thenReturn(Optional.of(existing));
      when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(a -> a.getArgument(0));

      SubscriptionDto result = subscriptionService.save(toSave);

      verify(subscriptionRepository).save(captureSaved.capture());
      verify(userService).getCurrentUser();
      verify(subscriptionRepository).findById(new SubscriptionId(userAgent, "login"));

      Subscription saved = captureSaved.getValue();
      assertAll(
          () -> assertThat(toSave).usingRecursiveComparison().as("result").isEqualTo(result),
          () -> assertEquals("end", saved.getEndpoint(), "Endpoint"),
          () -> assertEquals("public2", saved.getPublicKey(), "PublicKey"),
          () -> assertEquals("private2", saved.getPrivateKey(), "PrivateKey"),
          () -> assertEquals(56L, saved.getExpirationTime(), "ExpirationTime"),
          () -> assertEquals("userAgent", saved.getId().getUserAgent(), "UserAgent"),
          () -> assertEquals("login2", saved.getUser().getLogin(), "login"),
          () -> assertEquals("mdp", saved.getUser().getPassword(), "password"),
          () -> assertEquals("Paris", saved.getUser().getFavouriteLocation(), "location"),
          () -> assertEquals(TZ, saved.getUser().getTimezone(), "timezone"));
    }

    @Test
    void create() {
      ArgumentCaptor<Subscription> captureSaved = ArgumentCaptor.forClass(Subscription.class);
      String userAgent = "userAgent";
      SubscriptionDto toSave =
          NotificationUtils.buildSubscriptionDto(
              userAgent, "end", "public2", "private2", 56L, null);

      when(userService.getCurrentUser())
          .thenReturn(new User("login", "pwd", "Lyon", Language.FRENCH, TZ));
      when(subscriptionRepository.findById(new SubscriptionId(userAgent, "login")))
          .thenReturn(Optional.empty());
      when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(a -> a.getArgument(0));

      SubscriptionDto result = subscriptionService.save(toSave);

      verify(subscriptionRepository).save(captureSaved.capture());
      verify(userService).getCurrentUser();
      verify(subscriptionRepository).findById(new SubscriptionId(userAgent, "login"));

      Subscription saved = captureSaved.getValue();
      assertAll(
          () -> assertThat(toSave).usingRecursiveComparison().as("result").isEqualTo(result),
          () -> assertEquals("end", saved.getEndpoint(), "Endpoint"),
          () -> assertEquals("public2", saved.getPublicKey(), "PublicKey"),
          () -> assertEquals("private2", saved.getPrivateKey(), "PrivateKey"),
          () -> assertEquals(56L, saved.getExpirationTime(), "ExpirationTime"),
          () -> assertEquals("userAgent", saved.getId().getUserAgent(), "UserAgent"),
          () -> assertEquals("login", saved.getUser().getLogin(), "login"),
          () -> assertEquals("pwd", saved.getUser().getPassword(), "password"),
          () -> assertEquals("Lyon", saved.getUser().getFavouriteLocation(), "location"),
          () -> assertEquals(TZ, saved.getUser().getTimezone(), "timezone"));
    }
  }

  @Nested
  class Delete {
    @Test
    void ok() {
      try (MockedStatic<JwtTokenProvider> jwtTokenProvider = mockStatic(JwtTokenProvider.class)) {
        String userAgent = "ua";
        String login = "username";

        jwtTokenProvider
            .when(() -> JwtTokenProvider.getCurrentUserLogin())
            .thenReturn(Optional.of(login));
        doNothing().when(subscriptionRepository).deleteOthersByUserId(login, userAgent);

        assertDoesNotThrow(() -> subscriptionService.deleteOthersByUserId(userAgent));

        jwtTokenProvider.verify(() -> JwtTokenProvider.getCurrentUserLogin());
        verify(subscriptionRepository).deleteOthersByUserId(login, userAgent);
      }
    }

    @Test
    void given_user_not_found_when_deleting_then_exception() {
      try (MockedStatic<JwtTokenProvider> jwtTokenProvider = mockStatic(JwtTokenProvider.class)) {
        jwtTokenProvider
            .when(() -> JwtTokenProvider.getCurrentUserLogin())
            .thenReturn(Optional.empty());

        assertThrows(
            UsernameNotFoundException.class, () -> subscriptionService.deleteOthersByUserId("ua"));

        jwtTokenProvider.verify(() -> JwtTokenProvider.getCurrentUserLogin());
        verify(subscriptionRepository, never()).deleteOthersByUserId(any(), any());
      }
    }
  }
}
