package pmb.weatherwatcher.alert.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pmb.weatherwatcher.alert.model.Alert;

/**
 * {@link Alert} repository
 *
 * @see JpaRepository
 */
@Repository
@Transactional(readOnly = true)
public interface AlertRepository extends JpaRepository<Alert, Long> {

  @EntityGraph(attributePaths = {"triggerDays", "monitoredHours"})
  List<Alert> findDistinctByUserLogin(String login);

  Optional<Alert> findByIdAndUserLogin(Long id, String login);
}
