package pmb.weatherwatcher.user.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pmb.weatherwatcher.notification.model.Subscription;

/** User database entity. */
@Entity
@Table(name = "user")
public class User {

  @Id private String login;

  private String password;

  @Column(name = "favourite_location")
  private String favouriteLocation;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<Subscription> subscription;

  public User() {}

  /**
   * {@link User} constructor.
   *
   * @param login user's name
   * @param password his password
   * @param favouriteLocation suggested location when needed location
   */
  public User(String login, String password, String favouriteLocation) {
    this.login = login;
    this.password = password;
    this.favouriteLocation = favouriteLocation;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFavouriteLocation() {
    return favouriteLocation;
  }

  public void setFavouriteLocation(String favouriteLocation) {
    this.favouriteLocation = favouriteLocation;
  }

  public List<Subscription> getSubscription() {
    return Optional.ofNullable(subscription).orElse(new ArrayList<>());
  }

  public void setSubscription(List<Subscription> subscription) {
    this.subscription = subscription;
  }
}
