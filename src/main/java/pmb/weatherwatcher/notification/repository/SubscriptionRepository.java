package pmb.weatherwatcher.notification.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pmb.weatherwatcher.notification.model.Subscription;

/**
 * {@link Subscription} repository
 *
 * @see JpaRepository
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

  Optional<Subscription> findByUserAgentAndUserLogin(String userAgent, String login);
}
