package pmb.weatherwatcher.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pmb.weatherwatcher.user.model.User;

/** Subscription entity, when user subscribe to receive notifications */
@Entity
@Table(name = "subscription")
public class Subscription {

  @Id
  @Column(name = "user_agent")
  private String userAgent;

  private String endpoint;

  @Column(name = "expiration_time")
  private Long expirationTime;

  @ManyToOne
  @JoinColumn(name = "user", referencedColumnName = "login")
  private User user;

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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
