package pmb.weatherwatcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pmb.weatherwatcher.model.Alert;

/**
 * {@link Alert} repository
 *
 * @see JpaRepository
 */
@Repository
public interface AlertRepository
extends JpaRepository<Alert, Long> {

    List<Alert> findByUserLogin(String login);

}
