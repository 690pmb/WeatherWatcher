package pmb.weatherwatcher.alert.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  Page<Alert> findDistinctByUserLogin(String login, Pageable pageable);

  Optional<Alert> findByIdAndUserLogin(Long id, String login);

  @EntityGraph(attributePaths = {"triggerDays", "monitoredHours", "monitoredFields", "user"})
  List<Alert> findAllByTriggerDaysAndTriggerHour(DayOfWeek triggerDay, LocalTime triggerHour);
}
