package pmb.weatherwatcher.service;

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
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pmb.weatherwatcher.dto.JwtTokenDto;
import pmb.weatherwatcher.dto.UserDto;
import pmb.weatherwatcher.exception.AlreadyExistException;
import pmb.weatherwatcher.model.User;
import pmb.weatherwatcher.repository.UserRepository;
import pmb.weatherwatcher.security.JwtTokenProvider;

@Import(UserService.class)
@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserService userService;

    @Nested
    class Save {

        @Test
        void alreadyExist() {
            UserDto user = new UserDto("test", "pwd", "lyon");

            when(userRepository.findById("test")).thenReturn(Optional.of(new User()));

            assertThrows(AlreadyExistException.class, () -> userService.save(user));

            verify(userRepository).findById("test");
            verify(userRepository, never()).save(any());
            verify(bCryptPasswordEncoder, never()).encode(any());
            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        void success() {
            UserDto user = new UserDto("test", "pwd", "lyon");

            when(userRepository.findById("test")).thenReturn(Optional.empty());
            when(bCryptPasswordEncoder.encode("test")).thenAnswer(a -> a.getArgument(0));
            when(userRepository.save(any())).thenAnswer(a -> a.getArgument(0));

            UserDto saved = userService.save(user);

            assertAll(() -> assertNotNull(saved), () -> assertEquals("test", saved.getUsername()),
                    () -> assertEquals("lyon", saved.getFavouriteLocation()), () -> assertNull(saved.getPassword()),
                    () -> assertTrue(saved.isEnabled()), () -> assertTrue(saved.isAccountNonLocked()),
                    () -> assertTrue(saved.isCredentialsNonExpired()), () -> assertTrue(saved.isAccountNonExpired()));

            verify(userRepository).findById("test");
            verify(userRepository).save(any());
            verify(bCryptPasswordEncoder).encode("pwd");
            verify(authenticationManager, never()).authenticate(any());
        }

    }

    @Nested
    class Login {

        @Test
        void badCredentials() {
            ArgumentCaptor<UsernamePasswordAuthenticationToken> token = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            UserDto user = new UserDto("test", "pwd", "lyon");

            when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

            assertThrows(BadCredentialsException.class, () -> userService.login(user));

            verify(authenticationManager).authenticate(token.capture());
            verify(userRepository, never()).findById(any());
            verify(bCryptPasswordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());
            verify(jwtTokenProvider, never()).create(any());

            UsernamePasswordAuthenticationToken captured = token.getValue();
            assertAll(() -> assertEquals("test", captured.getName()), () -> assertEquals("pwd", captured.getCredentials()),
                    () -> assertFalse(captured.isAuthenticated()), () -> assertNull(SecurityContextHolder.getContext().getAuthentication()));
        }

        @Test
        void success() {
            ArgumentCaptor<UsernamePasswordAuthenticationToken> token = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            UserDto user = new UserDto("test", "pwd", "lyon");

            when(authenticationManager.authenticate(any())).thenAnswer(a -> a.getArgument(0));
            when(jwtTokenProvider.create(any())).thenReturn("jwt");

            JwtTokenDto login = userService.login(user);

            verify(authenticationManager).authenticate(token.capture());
            verify(jwtTokenProvider).create(any());
            verify(userRepository, never()).findById(any());
            verify(bCryptPasswordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());

            UsernamePasswordAuthenticationToken captured = token.getValue();
            assertAll(() -> assertEquals("jwt", login.getToken()), () -> assertEquals("test", captured.getName()),
                    () -> assertEquals("pwd", captured.getCredentials()), () -> assertFalse(captured.isAuthenticated()),
                    () -> assertEquals("test", SecurityContextHolder.getContext().getAuthentication().getName()));
        }

    }

}
