package pmb.weatherwatcher.notification.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pmb.weatherwatcher.notification.model.Subscription;
import pmb.weatherwatcher.notification.model.SubscriptionId;

/**
 * {@link Subscription} repository
 *
 * @see JpaRepository
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {

  Optional<Subscription> findById(SubscriptionId id);
}
