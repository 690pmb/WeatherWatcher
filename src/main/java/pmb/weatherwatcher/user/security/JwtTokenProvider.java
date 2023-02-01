package pmb.weatherwatcher.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.user.dto.UserDto;

/** Jwt token utils class. */
@Component
public class JwtTokenProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

  private static final String FAVOURITE_LOCATION = "location";
  private static final String LANGUAGE = "lang";

  private final String secretKey;
  private final Integer tokenDuration;

  public JwtTokenProvider(
      @Value("${jwt.secretkey}") String secretKey,
      @Value("${jwt.duration}") Integer tokenDuration) {
    this.secretKey = secretKey;
    this.tokenDuration = tokenDuration;
  }

  /**
   * Creates a token with authenticated user data.
   *
   * @param authentication user data
   * @return a token
   */
  public String create(Authentication authentication) {
    Date issuedAt = new Date();
    UserDto user = (UserDto) authentication.getPrincipal();
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim(FAVOURITE_LOCATION, user.getFavouriteLocation())
        .claim(LANGUAGE, user.getLang().getCode())
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .setId(UUID.randomUUID().toString())
        .setIssuedAt(issuedAt)
        .setExpiration(DateUtils.addSeconds(issuedAt, tokenDuration))
        .compact();
  }

  /**
   * Extracts user's name from given token
   *
   * @param token a jwt token
   * @return its username
   */
  public String getUserName(String token) {
    return parseToken(token).getBody().getSubject();
  }

  /**
   * Validates a jwt token.
   *
   * @param token to validate
   * @return {@code true} token valid, {@code false} otherwise
   */
  public boolean isValid(String token) {
    try {
      parseToken(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      LOGGER.debug("Invalid token", e);
      SecurityContextHolder.clearContext();
      return false;
    }
  }

  /**
   * Return an Authentication object of the given token.
   *
   * @param token containing valid credential user
   * @return a {@link UsernamePasswordAuthenticationToken} authenticated
   */
  public Authentication getAuthentication(String token) {
    Claims claims = parseToken(token).getBody();
    return new UsernamePasswordAuthenticationToken(
        new UserDto(
            claims.getSubject(),
            null,
            claims.get(FAVOURITE_LOCATION, String.class),
            Language.fromCode(claims.get(LANGUAGE, String.class)).get()),
        token,
        null);
  }

  /**
   * Extracts from Spring Security session the authenticated user's name.
   *
   * @return a username
   */
  public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (null == authentication) {
      return Optional.empty();
    }
    if (authentication.getPrincipal() instanceof UserDetails) {
      return Optional.of(((UserDetails) authentication.getPrincipal()).getUsername());
    }
    if (authentication.getPrincipal() instanceof String) {
      return Optional.of((String) authentication.getPrincipal());
    }
    return Optional.empty();
  }

  private Jws<Claims> parseToken(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
  }
}
