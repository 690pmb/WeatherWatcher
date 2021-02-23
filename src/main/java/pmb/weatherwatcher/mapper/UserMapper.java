package pmb.weatherwatcher.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pmb.weatherwatcher.dto.user.UserDto;
import pmb.weatherwatcher.model.User;

/**
 * Maps {@link UserDto} with {@link User}.
 */
@Mapper
public interface UserMapper {

    @Mapping(target = "username", source = "login")
    UserDto toDto(User user);

    @InheritInverseConfiguration
    User toEntity(UserDto dto);

}
