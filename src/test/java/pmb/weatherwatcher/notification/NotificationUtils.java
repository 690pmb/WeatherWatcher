package pmb.weatherwatcher.notification;

import pmb.weatherwatcher.notification.dto.SubscriptionDto;

public final class NotificationUtils {
  private NotificationUtils() {}

  public static SubscriptionDto buildSubscriptionDto(
      String userAgent, String endpoint, String publicKey, String privateKey, Long expirationTime) {
    SubscriptionDto sub = new SubscriptionDto();
    sub.setUserAgent(userAgent);
    sub.setEndpoint(endpoint);
    sub.setPublicKey(publicKey);
    sub.setPrivateKey(privateKey);
    sub.setExpirationTime(expirationTime);
    return sub;
  }
}
