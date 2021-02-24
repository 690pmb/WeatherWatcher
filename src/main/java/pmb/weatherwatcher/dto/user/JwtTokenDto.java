package pmb.weatherwatcher.dto.user;

/**
 * Holding authentication user token.
 */
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
