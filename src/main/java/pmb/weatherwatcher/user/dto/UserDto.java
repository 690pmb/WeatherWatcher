package pmb.weatherwatcher.user.dto;

import java.util.ArrayList;
import java.util.Collection;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pmb.weatherwatcher.common.model.Language;

/**
 * User data, used for authentication and registration.
 *
 * @see UserDetails
 */
public class UserDto implements UserDetails {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Size(min = 4, max = 30, groups = OnSignup.class)
  private String username;

  @NotNull
  @Size(min = 6, max = 30, groups = OnSignup.class)
  private String password;

  private String favouriteLocation;

  @NotNull(groups = OnSignup.class)
  private Language lang;

  @NotBlank(groups = OnSignup.class)
  private String timezone;

  public UserDto() {
    super();
  }

  public UserDto(
      String username, String password, String favouriteLocation, Language lang, String timezone) {
    this.username = username;
    this.password = password;
    this.favouriteLocation = favouriteLocation;
    this.lang = lang;
    this.timezone = timezone;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
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

  public Language getLang() {
    return lang;
  }

  public void setLang(Language lang) {
    this.lang = lang;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return new ArrayList<>();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
}
