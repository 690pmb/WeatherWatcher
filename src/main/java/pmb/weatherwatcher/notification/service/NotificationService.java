package pmb.weatherwatcher.notification.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pmb.weatherwatcher.notification.config.NotificationProperties;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;

@Service
public class NotificationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  private PushService pushService;
  private NotificationProperties notificationProperties;

  NotificationService(NotificationProperties notificationProperties)
      throws GeneralSecurityException {
    this.notificationProperties = notificationProperties;
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
    this.pushService =
        new PushService(
            this.notificationProperties.getPublicKey(),
            this.notificationProperties.getPrivateKey(),
            "subject");
  }

  public List<HttpStatus> send(List<SubscriptionDto> subscriptions, byte[] payload) {
    return subscriptions.stream()
        .map(
            sub -> {
              try {
                Notification notification =
                    new Notification(
                        sub.getEndpoint(),
                        encryptPublicKey(sub.getPublicKey()),
                        Base64.getDecoder().decode(sub.getPrivateKey()),
                        payload);
                HttpResponse pushResult = this.pushService.send(notification);
                if (Optional.ofNullable(pushResult.getEntity())
                        .map(HttpEntity::getContentLength)
                        .orElse(0L)
                    > 0) {
                  LOGGER.info(
                      "Push result: {}",
                      new BufferedReader(new InputStreamReader(pushResult.getEntity().getContent()))
                          .lines()
                          .collect(Collectors.joining("\n")));
                }
                return HttpStatus.resolve(pushResult.getStatusLine().getStatusCode());
              } catch (GeneralSecurityException
                  | IOException
                  | JoseException
                  | ExecutionException
                  | InterruptedException e) {
                LOGGER.error("Error when sending notification", e);
                return HttpStatus.INTERNAL_SERVER_ERROR;
              }
            })
        .distinct()
        .collect(Collectors.toList());
  }

  private PublicKey encryptPublicKey(String pub)
      throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
    KeyFactory kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
    ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
    ECPoint point = ecSpec.getCurve().decodePoint(Base64.getDecoder().decode(pub));
    ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);
    return kf.generatePublic(pubSpec);
  }
}
