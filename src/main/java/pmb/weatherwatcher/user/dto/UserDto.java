package pmb.weatherwatcher.user.dto;

import java.util.ArrayList;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User data, used for authentication and registration.
 *
 * @see UserDetails
 */
public class UserDto implements UserDetails {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Size(min = 4, max = 30)
  private String username;

  @NotNull
  @Size(min = 6, max = 30)
  private String password;

  private String favouriteLocation;

  public UserDto(String username, String password, String favouriteLocation) {
    this.username = username;
    this.password = password;
    this.favouriteLocation = favouriteLocation;
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
}
