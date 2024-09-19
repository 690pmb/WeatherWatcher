package pmb.weatherwatcher.notification.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.notification.NotificationUtils;
import pmb.weatherwatcher.notification.config.NotificationProperties;
import pmb.weatherwatcher.notification.dto.Operation;
import pmb.weatherwatcher.notification.dto.PayloadDataDto;
import pmb.weatherwatcher.notification.dto.PayloadDto;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;

@ServiceTestRunner
@Import({NotificationService.class, ObjectMapper.class})
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = NotificationProperties.class)
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
                null,
                "user"),
            NotificationUtils.buildSubscriptionDto(
                "ua2",
                "end2",
                notificationProperties.getPublicKey(),
                notificationProperties.getPrivateKey(),
                8L,
                "user"));
    ReflectionTestUtils.setField(notificationService, "pushService", pushService);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(pushService);
  }

  @Nested
  class construct {

    @ParameterizedTest(
        name = "Construct NotificationService with public: ''{0}'' and private: ''{1}''")
    @CsvSource({",", "'',''", ",test", "test,", "'',test", "test,''"})
    void when_invalid_properties_then_no_push_service(String publicKey, String privateString) {
      NotificationProperties prop = new NotificationProperties();
      prop.setPrivateKey(privateString);
      prop.setPublicKey(publicKey);
      assertDoesNotThrow(
          () -> {
            NotificationService service = new NotificationService(prop);
            assertNull(ReflectionTestUtils.getField(service, null, "pushService"));
          });
    }

    @Test
    void ok() throws GeneralSecurityException {
      String k1 = "public";
      String k2 = "private";
      NotificationProperties prop = new NotificationProperties();
      prop.setPublicKey(k1);
      prop.setPrivateKey(k2);
      try (MockedConstruction<PushService> mock = mockConstruction(PushService.class)) {
        new PushService(k1, k2);

        assertDoesNotThrow(
            () -> {
              NotificationService service = new NotificationService(prop);
              assertNotNull(ReflectionTestUtils.getField(service, null, "pushService"));
            });
      }
    }
  }

  @Nested
  class send {

    @Test
    void when_no_push_service()
        throws GeneralSecurityException,
            IOException,
            JoseException,
            ExecutionException,
            InterruptedException {
      ReflectionTestUtils.setField(notificationService, "pushService", null);

      List<HttpStatus> actual =
          notificationService.send(subscriptions, objectMapper.writeValueAsBytes(PAYLOAD));

      verify(pushService, never()).send(any());

      assertAll(
          () -> assertEquals(1, actual.size(), "size"),
          () -> assertEquals(HttpStatus.NO_CONTENT, actual.get(0), "status"));
    }

    @Test
    void ok()
        throws GeneralSecurityException,
            IOException,
            JoseException,
            ExecutionException,
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
        throws GeneralSecurityException,
            IOException,
            JoseException,
            ExecutionException,
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
