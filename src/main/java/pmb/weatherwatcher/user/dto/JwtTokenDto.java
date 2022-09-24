package pmb.weatherwatcher.user.dto;

/** Holding authentication user token. */
public class JwtTokenDto {

  private String token;

  public JwtTokenDto() {
    super();
  }

  public JwtTokenDto(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
