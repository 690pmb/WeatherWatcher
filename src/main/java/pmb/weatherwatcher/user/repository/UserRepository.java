package pmb.weatherwatcher.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pmb.weatherwatcher.user.model.User;

/**
 * {@link User} repository
 *
 * @see JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {}
