package pmb.weatherwatcher.user.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pmb.weatherwatcher.ServiceTestRunner;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.user.dto.UserDto;
import pmb.weatherwatcher.user.mapper.UserMapperImpl;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.repository.UserRepository;

@ServiceTestRunner
@Import({MyUserDetailsService.class, UserMapperImpl.class})
class MyUserDetailsServiceTest {

  private static final String TZ = "Europe/Paris";

  @MockBean private UserRepository userRepository;
  @Autowired private MyUserDetailsService myUserDetailsService;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(userRepository);
  }

  @Nested
  class LoadUserByUsername {

    @Test
    void ok() {
      when(userRepository.findById("test"))
          .thenReturn(Optional.of(new User("test", "pwd", "lyon", Language.FRENCH, TZ)));

      UserDto actual = (UserDto) myUserDetailsService.loadUserByUsername("test");

      assertAll(
          () -> assertEquals("test", actual.getUsername()),
          () -> assertEquals("pwd", actual.getPassword()),
          () -> assertEquals("lyon", actual.getFavouriteLocation()),
          () -> assertEquals("fr", actual.getLang().getCode()),
          () -> assertEquals(TZ, actual.getTimezone()));

      verify(userRepository).findById("test");
    }

    @Test
    void not_found() {
      when(userRepository.findById("test")).thenReturn(Optional.empty());

      assertThrows(
          UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername("test"));

      verify(userRepository).findById("test");
    }
  }

  @Nested
  class UpdatePassword {

    @Test
    void ok() {
      UserDto dto = new UserDto("test", "pwd", "lyon", Language.FRENCH, TZ);

      when(userRepository.findById("test"))
          .thenReturn(Optional.of(new User("test", "pwd", "lyon", Language.FRENCH, TZ)));
      when(userRepository.save(any())).thenAnswer(a -> a.getArgument(0));

      UserDto actual = (UserDto) myUserDetailsService.updatePassword(dto, "password");

      assertAll(
          () -> assertEquals("test", actual.getUsername()),
          () -> assertEquals("password", actual.getPassword()),
          () -> assertEquals("lyon", actual.getFavouriteLocation()),
          () -> assertEquals("fr", actual.getLang().getCode()),
          () -> assertEquals(TZ, actual.getTimezone()));

      verify(userRepository).findById("test");
      verify(userRepository).save(any());
    }

    @Test
    void not_found() {
      UserDto dto = new UserDto("test", "pwd", "lyon", Language.FRENCH, TZ);

      when(userRepository.findById("test")).thenReturn(Optional.empty());

      assertThrows(
          UsernameNotFoundException.class,
          () -> myUserDetailsService.updatePassword(dto, "password"));

      verify(userRepository).findById("test");
      verify(userRepository, never()).save(any());
    }
  }
}
