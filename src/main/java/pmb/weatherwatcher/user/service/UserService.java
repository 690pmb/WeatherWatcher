package pmb.weatherwatcher.user.service;

import java.time.ZoneId;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pmb.weatherwatcher.common.exception.AlreadyExistException;
import pmb.weatherwatcher.common.exception.BadRequestException;
import pmb.weatherwatcher.user.dto.EditUserDto;
import pmb.weatherwatcher.user.dto.JwtTokenDto;
import pmb.weatherwatcher.user.dto.PasswordDto;
import pmb.weatherwatcher.user.dto.UserDto;
import pmb.weatherwatcher.user.mapper.UserMapper;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.repository.UserRepository;
import pmb.weatherwatcher.user.security.JwtTokenProvider;

/** {@link User} service. */
@Service
public class UserService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserMapper userMapper;

  public UserService(
      UserRepository userRepository,
      AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userMapper = userMapper;
  }

  /**
   * Checks unity by username and save it to database (with its password encoded).
   *
   * @param user to save
   * @return saved user
   */
  public UserDto save(UserDto user) {
    if (!ZoneId.getAvailableZoneIds().contains(user.getTimezone())) {
      throw new BadRequestException("Invalid timezone: " + user.getTimezone());
    }
    userRepository
        .findById(user.getUsername())
        .ifPresent(
            u -> {
              throw new AlreadyExistException(
                  "User with name '" + user.getUsername() + "' already exist");
            });
    User saved =
        userRepository.save(
            new User(
                user.getUsername(),
                bCryptPasswordEncoder.encode(user.getPassword()),
                Optional.ofNullable(user.getFavouriteLocation())
                    .map(StringUtils::trim)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null),
                user.getLang(),
                user.getTimezone()));
    return userMapper.toDtoWithoutPassword(saved);
  }

  /**
   * Checks given credentials, authenticates and creates a jwt token.
   *
   * @param user credentials
   * @return a jwt token
   */
  public JwtTokenDto login(UserDto user) {
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    Authentication authentication = authenticationManager.authenticate(token);
    JwtTokenDto jwtToken = new JwtTokenDto(jwtTokenProvider.create(authentication));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return jwtToken;
  }

  /**
   * Checks if old password is correct and then updates user with new password.
   *
   * @param password holding new & old passwords
   */
  public void updatePassword(PasswordDto password) {
    User user = getCurrentUser();
    if (!bCryptPasswordEncoder.matches(password.getOldPassword(), user.getPassword())) {
      throw new BadCredentialsException("Invalid credentials");
    }
    user.setPassword(bCryptPasswordEncoder.encode(password.getNewPassword()));
    userRepository.save(user);
  }

  /**
   * Recovers current logged user.
   *
   * @return {@link User} authenticated
   */
  public User getCurrentUser() {
    return JwtTokenProvider.getCurrentUserLogin()
        .flatMap(userRepository::findById)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  /**
   * Edits current user's properties with given information.
   *
   * @param editUser new information
   * @return user updated
   */
  public JwtTokenDto edit(EditUserDto editUser) {
    if (ObjectUtils.allNull(
        editUser.getFavouriteLocation(), editUser.getLang(), editUser.getTimezone())) {
      throw new BadRequestException("At least one field must be filled when editing a user");
    }
    if (editUser.getTimezone() != null
        && !ZoneId.getAvailableZoneIds().contains(editUser.getTimezone())) {
      throw new BadRequestException("Invalid timezone: " + editUser.getTimezone());
    }
    return new JwtTokenDto(
        jwtTokenProvider.create(
            new UsernamePasswordAuthenticationToken(
                userMapper.toDtoWithoutPassword(
                    userRepository.save(userMapper.edit(getCurrentUser(), editUser))),
                null)));
  }
}
