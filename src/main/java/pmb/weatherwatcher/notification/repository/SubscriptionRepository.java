package pmb.weatherwatcher.notification.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  @Modifying
  @Query("delete from Subscription s where s.id.user = :user and s.id.userAgent <> :userAgent")
  void deleteOthersByUserId(@Param("user") String user, @Param("userAgent") String userAgent);
}
