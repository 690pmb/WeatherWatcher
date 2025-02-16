package pmb.weatherwatcher.user.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.common.exception.AlreadyExistException;
import pmb.weatherwatcher.common.exception.BadRequestException;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.user.dto.EditUserDto;
import pmb.weatherwatcher.user.dto.JwtTokenDto;
import pmb.weatherwatcher.user.dto.PasswordDto;
import pmb.weatherwatcher.user.dto.UserDto;
import pmb.weatherwatcher.user.mapper.UserMapperImpl;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.repository.UserRepository;
import pmb.weatherwatcher.user.security.JwtTokenProvider;

@Import({UserService.class, UserMapperImpl.class})
@ServiceTestRunner
class UserServiceTest {

  @MockBean private UserRepository userRepository;
  @MockBean private AuthenticationManager authenticationManager;
  @MockBean private JwtTokenProvider jwtTokenProvider;
  @MockBean private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired private UserService userService;

  private static final String TZ = "Europe/Paris";
  private final UserDto DUMMY_USER = new UserDto("test", "pwd", "lyon", Language.FRENCH, TZ);
  private final PasswordDto DUMMY_PASSWORD = new PasswordDto("password", "newPassword");

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(
        userRepository, authenticationManager, jwtTokenProvider, bCryptPasswordEncoder);
  }

  @Nested
  class Save {

    @Test
    void already_exist() {
      when(userRepository.findById("test")).thenReturn(Optional.of(new User()));

      assertThrows(AlreadyExistException.class, () -> userService.save(DUMMY_USER));

      verify(userRepository).findById("test");
      verify(userRepository, never()).save(any());
      verify(bCryptPasswordEncoder, never()).encode(any());
      verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void invalid_time_zone() {
      assertThrows(
          BadRequestException.class,
          () -> userService.save(new UserDto("test", "pwd", "lyon", Language.FRENCH, "timezone")),
          "Invalid timezone: timezone");

      verify(userRepository, never()).findById("test");
      verify(userRepository, never()).save(any());
      verify(bCryptPasswordEncoder, never()).encode(any());
      verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void success() {
      when(userRepository.findById("test")).thenReturn(Optional.empty());
      when(bCryptPasswordEncoder.encode("test")).thenAnswer(a -> a.getArgument(0));
      when(userRepository.save(any())).thenAnswer(a -> a.getArgument(0));

      UserDto saved = userService.save(DUMMY_USER);

      assertAll(
          () -> assertNotNull(saved),
          () -> assertEquals("test", saved.getUsername()),
          () -> assertEquals("lyon", saved.getFavouriteLocation()),
          () -> assertEquals("fr", saved.getLang().getCode()),
          () -> assertEquals("Europe/Paris", saved.getTimezone()),
          () -> assertNull(saved.getPassword()),
          () -> assertTrue(saved.isEnabled()),
          () -> assertTrue(saved.isAccountNonLocked()),
          () -> assertTrue(saved.isCredentialsNonExpired()),
          () -> assertTrue(saved.isAccountNonExpired()));

      verify(userRepository).findById("test");
      verify(userRepository).save(any());
      verify(bCryptPasswordEncoder).encode("pwd");
      verify(authenticationManager, never()).authenticate(any());
    }
  }

  @Nested
  class Login {

    @Test
    void bad_credentials() {
      ArgumentCaptor<UsernamePasswordAuthenticationToken> token =
          ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

      when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

      assertThrows(BadCredentialsException.class, () -> userService.login(DUMMY_USER));

      verify(authenticationManager).authenticate(token.capture());
      verify(userRepository, never()).findById(any());
      verify(bCryptPasswordEncoder, never()).encode(any());
      verify(userRepository, never()).save(any());
      verify(jwtTokenProvider, never()).create(any());

      UsernamePasswordAuthenticationToken captured = token.getValue();
      assertAll(
          () -> assertEquals("test", captured.getName()),
          () -> assertEquals("pwd", captured.getCredentials()),
          () -> assertFalse(captured.isAuthenticated()),
          () -> assertNull(SecurityContextHolder.getContext().getAuthentication()));
    }

    @Test
    void success() {
      ArgumentCaptor<UsernamePasswordAuthenticationToken> token =
          ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

      when(authenticationManager.authenticate(any())).thenAnswer(a -> a.getArgument(0));
      when(jwtTokenProvider.create(any())).thenReturn("jwt");

      JwtTokenDto login = userService.login(DUMMY_USER);

      verify(authenticationManager).authenticate(token.capture());
      verify(jwtTokenProvider).create(any());
      verify(userRepository, never()).findById(any());
      verify(bCryptPasswordEncoder, never()).encode(any());
      verify(userRepository, never()).save(any());

      UsernamePasswordAuthenticationToken captured = token.getValue();
      assertAll(
          () -> assertEquals("jwt", login.getToken()),
          () -> assertEquals("test", captured.getName()),
          () -> assertEquals("pwd", captured.getCredentials()),
          () -> assertFalse(captured.isAuthenticated()),
          () ->
              assertEquals(
                  "test", SecurityContextHolder.getContext().getAuthentication().getName()));
    }
  }

  @Nested
  class UpdatePassword {

    @Test
    @WithMockUser(username = "test")
    void ok() {
      User user = new User("test", "encryptedPassword", "lyon", Language.FRENCH, TZ);
      ArgumentCaptor<User> captured = ArgumentCaptor.forClass(User.class);

      when(userRepository.findById("test")).thenReturn(Optional.of(user));
      when(bCryptPasswordEncoder.matches("password", "encryptedPassword")).thenReturn(true);
      when(bCryptPasswordEncoder.encode("newPassword")).thenAnswer(a -> a.getArgument(0));
      when(userRepository.save(any())).thenAnswer(a -> a.getArgument(0));

      userService.updatePassword(DUMMY_PASSWORD);

      verify(userRepository).findById("test");
      verify(bCryptPasswordEncoder).matches("password", "encryptedPassword");
      verify(bCryptPasswordEncoder).encode("newPassword");
      verify(userRepository).save(captured.capture());

      User savedUser = captured.getValue();
      assertAll(
          () -> assertEquals("lyon", savedUser.getFavouriteLocation()),
          () -> assertEquals("test", savedUser.getLogin()),
          () -> assertEquals("newPassword", savedUser.getPassword()));
    }

    @Test
    void not_loggued_then_not_found() {
      assertThrows(
          UsernameNotFoundException.class, () -> userService.updatePassword(DUMMY_PASSWORD));

      verify(userRepository, never()).findById("test");
      verify(bCryptPasswordEncoder, never()).matches("password", "encryptedPassword");
      verify(bCryptPasswordEncoder, never()).encode("newPassword");
      verify(userRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test")
    void not_found_in_db() {
      when(userRepository.findById("test")).thenReturn(Optional.empty());

      assertThrows(
          UsernameNotFoundException.class, () -> userService.updatePassword(DUMMY_PASSWORD));

      verify(userRepository).findById("test");
      verify(bCryptPasswordEncoder, never()).matches("password", "encryptedPassword");
      verify(bCryptPasswordEncoder, never()).encode("newPassword");
      verify(userRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test")
    void incorrect_password() {
      User user = new User("test", "encryptedPassword", "lyon", Language.FRENCH, TZ);

      when(userRepository.findById("test")).thenReturn(Optional.of(user));
      when(bCryptPasswordEncoder.matches("password", "encryptedPassword")).thenReturn(false);

      assertThrows(BadCredentialsException.class, () -> userService.updatePassword(DUMMY_PASSWORD));

      verify(userRepository).findById("test");
      verify(bCryptPasswordEncoder).matches("password", "encryptedPassword");
      verify(bCryptPasswordEncoder, never()).encode("newPassword");
      verify(userRepository, never()).save(any());
    }
  }

  @Nested
  class Edit {

    @ParameterizedTest(
        name =
            "Given location ''{0}'', lang ''{1}'' and timezone ''{2}'' when editing a user then all good")
    @WithMockUser(username = "test")
    @CsvSource(
        value = {
          "lyon,,",
          ",fr,",
          "lyon,fr,",
          "'',,",
          "lyon,,America/Araguaina",
          ",fr,America/Araguaina",
          "lyon,fr,America/Araguaina",
          "'',,America/Araguaina",
          ",,America/Araguaina"
        })
    void ok(String location, String lang, String timezone) {
      EditUserDto editUser =
          new EditUserDto(
              location,
              Optional.ofNullable(lang).flatMap(Language::fromCode).orElse(null),
              timezone);
      User currentUser = new User("test", "pwd2", "Paris", Language.GREEK, TZ);
      ArgumentCaptor<UsernamePasswordAuthenticationToken> token =
          ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
      ArgumentCaptor<User> save = ArgumentCaptor.forClass(User.class);

      when(userRepository.findById("test")).thenReturn(Optional.of(currentUser));
      when(jwtTokenProvider.create(any())).thenReturn("jwt");
      when(userRepository.save(any())).thenAnswer(a -> a.getArgument(0));

      JwtTokenDto newToken = userService.edit(editUser);

      verify(jwtTokenProvider).create(token.capture());
      verify(userRepository).findById("test");
      verify(userRepository).save(save.capture());

      assertAll(
          () -> assertEquals("jwt", newToken.getToken()),
          () ->
              assertEquals(
                  Optional.ofNullable(location).orElse("Paris"),
                  save.getValue().getFavouriteLocation()),
          () ->
              assertEquals(
                  Optional.ofNullable(lang).orElse("el"), save.getValue().getLang().getCode()),
          () ->
              assertEquals(Optional.ofNullable(timezone).orElse(TZ), save.getValue().getTimezone()),
          () -> assertEquals("test", save.getValue().getLogin()),
          () -> assertEquals("test", ((UserDto) token.getValue().getPrincipal()).getUsername()),
          () -> assertFalse(token.getValue().isAuthenticated()),
          () ->
              assertEquals(
                  "test", SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @Test
    @WithMockUser(username = "test")
    void given_invalid_timezone_when_edting_user_then_bad_request_exception() {
      assertThrows(
          BadRequestException.class,
          () -> userService.edit(new EditUserDto(null, null, "timezone")),
          "Invalid timezone: timezone");

      verify(jwtTokenProvider, never()).create(any());
      verify(userRepository, never()).findById(any());
      verify(userRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test")
    void given_all_fields_null_when_edting_user_then_bad_request_exception() {
      assertThrows(BadRequestException.class, () -> userService.edit(new EditUserDto()));

      verify(jwtTokenProvider, never()).create(any());
      verify(userRepository, never()).findById(any());
      verify(userRepository, never()).save(any());
    }
  }
}
