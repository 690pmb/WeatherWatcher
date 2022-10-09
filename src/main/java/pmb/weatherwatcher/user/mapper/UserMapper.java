package pmb.weatherwatcher.user.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pmb.weatherwatcher.common.mapper.EntityDtoMapper;
import pmb.weatherwatcher.user.dto.UserDto;
import pmb.weatherwatcher.user.model.User;

/** Maps {@link UserDto} with {@link User}. */
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityDtoMapper<User, UserDto> {

  @Override
  @Mapping(target = "username", source = "login")
  @Mapping(target = "authorities", ignore = true)
  UserDto toDto(User user);

  @Override
  @Mapping(target = "subscription", ignore = true)
  @InheritInverseConfiguration
  User toEntity(UserDto dto);
}
