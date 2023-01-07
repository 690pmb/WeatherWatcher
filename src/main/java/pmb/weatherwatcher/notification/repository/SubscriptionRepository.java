package pmb.weatherwatcher.notification.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  List<Subscription> findByUserLoginIn(Set<String> users);
}
