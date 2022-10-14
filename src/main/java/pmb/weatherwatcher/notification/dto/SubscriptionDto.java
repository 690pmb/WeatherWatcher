package pmb.weatherwatcher.notification.dto;

import javax.validation.constraints.NotNull;

public class SubscriptionDto {

  @NotNull private String userAgent;

  @NotNull private String endpoint;

  private Long expirationTime;

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public Long getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(Long expirationTime) {
    this.expirationTime = expirationTime;
  }
}
