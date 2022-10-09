package pmb.weatherwatcher.notification.service;

import org.springframework.stereotype.Service;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.mapper.SubscriptionMapper;
import pmb.weatherwatcher.notification.model.Subscription;
import pmb.weatherwatcher.notification.repository.SubscriptionRepository;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.service.UserService;

/** {@link Subscription} service. */
@Service
public class SubscriptionService {

  private SubscriptionRepository subscriptionRepository;
  private SubscriptionMapper subscriptionMapper;
  private UserService userService;

  public SubscriptionService(
      SubscriptionRepository subscriptionRepository,
      SubscriptionMapper subscriptionMapper,
      UserService userService) {
    this.subscriptionRepository = subscriptionRepository;
    this.subscriptionMapper = subscriptionMapper;
    this.userService = userService;
  }

  /**
   * Save a subscription to database.
   *
   * @param subscription to save
   * @return saved subscription
   */
  public SubscriptionDto save(SubscriptionDto subscription) {
    User currentUser = userService.getCurrentUser();
    Subscription toSave =
        subscriptionRepository
            .findByUserAgentAndUserLogin(subscription.getUserAgent(), currentUser.getLogin())
            .map(
                existing -> {
                  subscriptionMapper.updateFromDto(subscription, existing);
                  return existing;
                })
            .orElseGet(
                () -> {
                  Subscription sub = subscriptionMapper.toEntity(subscription);
                  sub.setUser(currentUser);
                  return sub;
                });
    return subscriptionMapper.toDto(subscriptionRepository.save(toSave));
  }
}
