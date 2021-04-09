package pmb.weatherwatcher.alert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pmb.weatherwatcher.alert.model.Alert;

/**
 * {@link Alert} repository
 *
 * @see JpaRepository
 */
@Repository
public interface AlertRepository
extends JpaRepository<Alert, Long> {

    List<Alert> findByUserLogin(String login);

    Optional<Alert> findByIdAndUserLogin(Long id, String login);

}
