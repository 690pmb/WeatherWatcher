package pmb.weatherwatcher.notification.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SubscriptionId implements Serializable {

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "user")
  private String user;

  public SubscriptionId() {}

  public SubscriptionId(String userAgent, String user) {
    this.userAgent = userAgent;
    this.user = user;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, userAgent);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SubscriptionId other = (SubscriptionId) obj;
    return Objects.equals(user, other.user) && Objects.equals(userAgent, other.userAgent);
  }
}
