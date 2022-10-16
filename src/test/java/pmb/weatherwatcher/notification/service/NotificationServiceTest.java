package pmb.weatherwatcher.notification.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pmb.weatherwatcher.notification.NotificationUtils;
import pmb.weatherwatcher.notification.config.NotificationProperties;
import pmb.weatherwatcher.notification.dto.Operation;
import pmb.weatherwatcher.notification.dto.PayloadDataDto;
import pmb.weatherwatcher.notification.dto.PayloadDto;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;

@ActiveProfiles("test")
@Import({NotificationService.class, ObjectMapper.class})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = NotificationProperties.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class NotificationServiceTest {

  @Mock private PushService pushService;
  @Autowired private NotificationService notificationService;
  @Autowired private NotificationProperties notificationProperties;
  @Autowired private ObjectMapper objectMapper;

  private List<SubscriptionDto> subscriptions;
  private static final PayloadDto PAYLOAD =
      new PayloadDto(
          "titre", "body", new PayloadDataDto(Operation.NAVIGATE_LAST_FOCUSED_OR_OPEN, "url"));
  private static final String PAYLOAD_JSON =
      "{\"notification\":{\"title\":\"titre\",\"body\":\"body\",\"data\":{\"onActionClick\":{\"default\":{\"operation\":\"navigateLastFocusedOrOpen\",\"url\":\"url\"}}},\"requireInteraction\":true}}";

  @BeforeEach
  void tearUp() {
    subscriptions =
        List.of(
            NotificationUtils.buildSubscriptionDto(
                "ua1",
                "end1",
                notificationProperties.getPublicKey(),
                notificationProperties.getPrivateKey(),
                null),
            NotificationUtils.buildSubscriptionDto(
                "ua2",
                "end2",
                notificationProperties.getPublicKey(),
                notificationProperties.getPrivateKey(),
                8L));
    ReflectionTestUtils.setField(notificationService, "pushService", pushService);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(pushService);
  }

  @Nested
  class send {
    @Test
    void ok()
        throws GeneralSecurityException, IOException, JoseException, ExecutionException,
            InterruptedException {
      ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
      HttpResponse httpResponse =
          new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 5, 1), 201, null));

      when(pushService.send(any())).thenReturn(httpResponse);

      List<HttpStatus> actual =
          notificationService.send(subscriptions, objectMapper.writeValueAsBytes(PAYLOAD));

      verify(pushService, times(2)).send(notifCaptor.capture());

      List<Notification> sentNotifications = notifCaptor.getAllValues();
      assertAll(
          () -> assertEquals(1, actual.size(), "size"),
          () -> assertEquals(HttpStatus.CREATED, actual.get(0), "status"),
          () -> assertEquals(2, sentNotifications.size(), "notif size"),
          () -> assertEquals("end1", sentNotifications.get(0).getEndpoint(), "notif1 endpoint"),
          () -> assertEquals("end2", sentNotifications.get(1).getEndpoint(), "notif1 endpoint"),
          () ->
              assertTrue(
                  sentNotifications.stream()
                      .map(Notification::getPayload)
                      .map(String::new)
                      .allMatch(Predicate.isEqual(PAYLOAD_JSON)),
                  "notif payload"));
    }

    @ParameterizedTest
    @ValueSource(
        classes = {
          GeneralSecurityException.class,
          IOException.class,
          JoseException.class,
          ExecutionException.class,
          InterruptedException.class
        })
    void when_send_fails(Class<? extends Throwable> throwableType)
        throws GeneralSecurityException, IOException, JoseException, ExecutionException,
            InterruptedException {
      when(pushService.send(any())).thenThrow(throwableType);

      List<HttpStatus> actual =
          notificationService.send(subscriptions, objectMapper.writeValueAsBytes(PAYLOAD));

      verify(pushService, times(2)).send(any());

      assertAll(
          () -> assertEquals(1, actual.size(), "size"),
          () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actual.get(0), "status"));
    }
  }
}
