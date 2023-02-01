package pmb.weatherwatcher.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.notification.NotificationUtils;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.mapper.SubscriptionMapperImpl;
import pmb.weatherwatcher.notification.model.Subscription;
import pmb.weatherwatcher.notification.model.SubscriptionId;
import pmb.weatherwatcher.notification.repository.SubscriptionRepository;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.service.UserService;

@ServiceTestRunner
@Import({SubscriptionService.class, SubscriptionMapperImpl.class})
class SubscriptionServiceTest {

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
      existing.setUser(new User("login2", "mdp", "Paris", Language.FRENCH));
      existing.setPublicKey("public");
      existing.setPrivateKey("private");

      when(userService.getCurrentUser())
          .thenReturn(new User("login", "pwd", "Lyon", Language.FRENCH));
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
          () -> assertEquals("Paris", saved.getUser().getFavouriteLocation(), "location"));
    }

    @Test
    void create() {
      ArgumentCaptor<Subscription> captureSaved = ArgumentCaptor.forClass(Subscription.class);
      String userAgent = "userAgent";
      SubscriptionDto toSave =
          NotificationUtils.buildSubscriptionDto(
              userAgent, "end", "public2", "private2", 56L, null);

      when(userService.getCurrentUser())
          .thenReturn(new User("login", "pwd", "Lyon", Language.FRENCH));
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
          () -> assertEquals("Lyon", saved.getUser().getFavouriteLocation(), "location"));
    }
  }
}
