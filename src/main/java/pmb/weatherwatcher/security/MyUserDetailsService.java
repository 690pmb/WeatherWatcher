package pmb.weatherwatcher.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pmb.weatherwatcher.dto.UserDto;
import pmb.weatherwatcher.repository.UserRepository;

/**
 * @see UserDetailsService
 */
@Service
public class MyUserDetailsService
        implements UserDetailsService {

    private UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return userRepository.findById(login).map(user -> new UserDto(user.getLogin(), user.getPassword(), user.getFavouriteLocation()))
                .orElseThrow(() -> new UsernameNotFoundException("user: " + login + " not found"));
    }

}
