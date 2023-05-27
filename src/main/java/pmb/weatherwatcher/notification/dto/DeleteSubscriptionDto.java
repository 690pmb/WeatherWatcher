package pmb.weatherwatcher.notification.dto;

import javax.validation.constraints.NotBlank;

public class DeleteSubscriptionDto {
  @NotBlank String userAgent;

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }
}
