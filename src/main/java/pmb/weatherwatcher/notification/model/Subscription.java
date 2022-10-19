package pmb.weatherwatcher.notification.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import pmb.weatherwatcher.user.model.User;

/** Subscription entity, when user subscribe to receive notifications */
@Entity
@Table(name = "subscription")
public class Subscription {

  @EmbeddedId private SubscriptionId id;

  @ManyToOne
  @MapsId("user")
  @JoinColumn(name = "user", referencedColumnName = "login")
  private User user;

  private String endpoint;

  @Column(name = "public_key")
  private String publicKey;

  @Column(name = "private_key")
  private String privateKey;

  @Column(name = "expiration_time")
  private Long expirationTime;

  public SubscriptionId getId() {
    return id;
  }

  public void setId(SubscriptionId id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public Long getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(Long expirationTime) {
    this.expirationTime = expirationTime;
  }
}
