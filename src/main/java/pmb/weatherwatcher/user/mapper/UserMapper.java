package pmb.weatherwatcher.user.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pmb.weatherwatcher.common.mapper.EntityDtoMapper;
import pmb.weatherwatcher.user.dto.EditUserDto;
import pmb.weatherwatcher.user.dto.UserDto;
import pmb.weatherwatcher.user.model.User;

/** Maps {@link UserDto} with {@link User}. */
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityDtoMapper<User, UserDto> {

  @Override
  @Mapping(target = "username", source = "login")
  @Mapping(target = "authorities", ignore = true)
  UserDto toDto(User user);

  @InheritConfiguration(name = "toDto")
  @Mapping(target = "password", ignore = true)
  UserDto toDtoWithoutPassword(User user);

  @Override
  @Mapping(target = "subscription", ignore = true)
  @InheritInverseConfiguration(name = "toDto")
  User toEntity(UserDto dto);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "subscription", ignore = true)
  @Mapping(target = "login", ignore = true)
  @Mapping(
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
      target = "lang")
  @Mapping(
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
      target = "favouriteLocation")
  User edit(@MappingTarget User user, EditUserDto editUser);
}
