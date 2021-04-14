package pmb.weatherwatcher.user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pmb.weatherwatcher.user.dto.UserDto;
import pmb.weatherwatcher.user.mapper.UserMapper;
import pmb.weatherwatcher.user.repository.UserRepository;

/**
 * @see UserDetailsService
 * @see UserDetailsPasswordService
 */
@Service
public class MyUserDetailsService
        implements UserDetailsService, UserDetailsPasswordService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public MyUserDetailsService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return userMapper.toOptionalDto(userRepository.findById(login))
                .orElseThrow(() -> new UsernameNotFoundException("user: " + login + " not found"));
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        UserDto dto = (UserDto) loadUserByUsername(user.getUsername());
        dto.setPassword(newPassword);
        return userMapper.toDto(userRepository.save(userMapper.toEntity(dto)));
    }

}
