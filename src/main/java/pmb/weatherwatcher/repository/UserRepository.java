package pmb.weatherwatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pmb.weatherwatcher.model.User;

/**
 * {@link User} repository
 *
 * @see JpaRepository
 */
@Repository
public interface UserRepository
        extends JpaRepository<User, String> {

}
