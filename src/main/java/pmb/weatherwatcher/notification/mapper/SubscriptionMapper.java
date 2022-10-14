package pmb.weatherwatcher.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pmb.weatherwatcher.common.mapper.EntityDtoMapper;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.model.Subscription;

/** Maps {@link Subscription} with {@link SubscriptionDto}. */
@Mapper(componentModel = "spring")
public interface SubscriptionMapper extends EntityDtoMapper<Subscription, SubscriptionDto> {

  @Override
  @Mapping(target = "user", ignore = true)
  Subscription toEntity(SubscriptionDto dto);

  @Mapping(target = "user", ignore = true)
  void updateFromDto(SubscriptionDto dto, @MappingTarget Subscription entity);
}
