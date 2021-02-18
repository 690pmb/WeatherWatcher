package pmb.weatherwatcher.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pmb.weatherwatcher.dto.UserDto;
import pmb.weatherwatcher.mapper.UserMapper;
import pmb.weatherwatcher.repository.UserRepository;

/**
 * @see UserDetailsService
 */
@Service
public class MyUserDetailsService
        implements UserDetailsService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public MyUserDetailsService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return userRepository.findById(login).map(userMapper::toDto)
                .orElseThrow(() -> new UsernameNotFoundException("user: " + login + " not found"));
    }

}
