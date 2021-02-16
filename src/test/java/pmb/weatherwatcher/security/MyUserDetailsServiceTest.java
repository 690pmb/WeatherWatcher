package pmb.weatherwatcher.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pmb.weatherwatcher.dto.UserDto;
import pmb.weatherwatcher.model.User;
import pmb.weatherwatcher.repository.UserRepository;

@DisplayNameGeneration(value = ReplaceUnderscores.class)
@Import(MyUserDetailsService.class)
@ExtendWith(SpringExtension.class)
class MyUserDetailsServiceTest {

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Test
    void loadUserByUsername_ok() {
        when(userRepository.findById("test")).thenReturn(Optional.of(new User("test", "pwd", "lyon")));

        UserDto actual = (UserDto) myUserDetailsService.loadUserByUsername("test");

        assertAll(() -> assertEquals("test", actual.getUsername()), () -> assertEquals("pwd", actual.getPassword()),
                () -> assertEquals("lyon", actual.getFavouriteLocation()));

        verify(userRepository).findById("test");
    }

    @Test
    void loadUserByUsername_not_found() {
        when(userRepository.findById("test")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername("test"));

        verify(userRepository).findById("test");
    }

}