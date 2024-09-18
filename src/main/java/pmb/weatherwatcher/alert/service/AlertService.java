package pmb.weatherwatcher.alert.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredDaysDto;
import pmb.weatherwatcher.alert.dto.MonitoredFieldDto;
import pmb.weatherwatcher.alert.mapper.AlertMapper;
import pmb.weatherwatcher.alert.model.Alert;
import pmb.weatherwatcher.alert.repository.AlertRepository;
import pmb.weatherwatcher.common.exception.BadRequestException;
import pmb.weatherwatcher.common.exception.NotFoundException;
import pmb.weatherwatcher.user.model.User;
import pmb.weatherwatcher.user.service.UserService;

/** {@link Alert} service. */
@Service
public class AlertService {

  private final AlertRepository alertRepository;
  private final AlertMapper alertMapper;
  private final UserService userService;

  public AlertService(
      AlertRepository alertRepository, AlertMapper alertMapper, UserService userService) {
    this.alertRepository = alertRepository;
    this.alertMapper = alertMapper;
    this.userService = userService;
  }

  private AlertDto save(AlertDto alert, User currentUser) {
    validate(alert);
    Alert toSave = alertMapper.toEntity(alert);
    toSave.setUser(Optional.ofNullable(currentUser).orElseGet(userService::getCurrentUser));
    return alertMapper.toDto(alertRepository.save(toSave));
  }

  /**
   * Validates and creates given alert for the currently logged user.
   *
   * @param alert to save
   * @return the saved alert
   */
  public AlertDto create(AlertDto alert) {
    if (alert.getId() != null
        || alert.getMonitoredFields().stream()
            .map(MonitoredFieldDto::getId)
            .anyMatch(Objects::nonNull)) {
      throw new BadRequestException("Ids must be null when creating an alert");
    }
    return save(alert, null);
  }

  private void validate(AlertDto alert) {
    alert
        .getMonitoredFields()
        .forEach(
            field -> {
              if (field.getMax() == null && field.getMin() == null) {
                throw new BadRequestException(
                    "Monitored field '"
                        + field.getField()
                        + "' has its min and max values undefined");
              } else if (field.getMax() != null
                  && field.getMin() != null
                  && field.getMin().compareTo(field.getMax()) > 0) {
                throw new BadRequestException(
                    "Monitored field '"
                        + field.getField()
                        + "' has its min value greater than its max value: '["
                        + field.getMin()
                        + ", "
                        + field.getMax()
                        + "]'");
              }
            });
    MonitoredDaysDto monitoredDays = alert.getMonitoredDays();
    if (Stream.of(
        monitoredDays.getNextDay(), monitoredDays.getSameDay(), monitoredDays.getTwoDayLater())
        .allMatch(BooleanUtils::isNotTrue)) {
      throw new BadRequestException("Given alert has no monitored days");
    }
  }

  /**
   * Finds all alerts for the currently logged user.
   *
   * @param pageable for pagination and sorting
   * @return a list of alerts
   */
  public Page<AlertDto> findAllForCurrentUser(Pageable pageable) {
    return alertMapper.toDtoPage(
        alertRepository.findDistinctByUserLogin(userService.getCurrentUser().getLogin(), pageable));
  }

  /**
   * Finds an alerts by its id.
   *
   * @param id identifier of the sought alert
   * @return the found alert
   */
  public AlertDto findById(Long id) {
    return alertMapper.toDto(
        alertRepository
            .findByIdAndUserLogin(id, userService.getCurrentUser().getLogin())
            .orElseThrow(() -> new NotFoundException("Alert with id: '" + id + "' was not found")));
  }

  /**
   * Finds alerts to trigger.
   *
   * @param triggerDay  day
   * @param triggerHour hour
   * @return a list of alerts
   */
  public List<AlertDto> findAllToTrigger(DayOfWeek triggerDay, LocalTime triggerHour) {
    return alertRepository.findAllByTriggerDaysAndTriggerHour(triggerDay, triggerHour.truncatedTo(ChronoUnit.SECONDS))
        .stream()
        .map(alertMapper::toDtoWithUser)
        .collect(Collectors.toList());
  }

  /**
   * Validates and updates the given alert for the currently logged user.
   *
   * @param alert new alert
   * @return the updated alert
   */
  public AlertDto update(AlertDto alert) {
    return Optional.ofNullable(alert.getId())
        .flatMap(
            id -> {
              User currentUser = userService.getCurrentUser();
              return alertRepository
                  .findByIdAndUserLogin(id, currentUser.getLogin())
                  .map(a -> currentUser);
            })
        .map(currentUser -> save(alert, currentUser))
        .orElseThrow(
            () -> new BadRequestException(
                "Alert to update with id '" + alert.getId() + "' for logged user is unknown"));
  }

  /**
   * Deletes alert having given id for the currently logged user.
   *
   * @param ids alert identifier to delete
   */
  public void delete(List<Long> ids) {
    ids.stream()
        .map(
            id -> alertRepository
                .findByIdAndUserLogin(id, userService.getCurrentUser().getLogin())
                .orElseThrow(
                    () -> new BadRequestException(
                        "Alert to delete with id '"
                            + id
                            + "' doesn't exist or doesn't belong to logged user")))
        .forEach(alertRepository::delete);
  }
}
