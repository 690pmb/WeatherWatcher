package pmb.weatherwatcher.alert.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.dto.MonitoredDaysDto;
import pmb.weatherwatcher.alert.mapper.AlertMapper;
import pmb.weatherwatcher.alert.model.Alert;
import pmb.weatherwatcher.alert.repository.AlertRepository;
import pmb.weatherwatcher.common.exception.BadRequestException;
import pmb.weatherwatcher.user.service.UserService;

/**
 * {@link Alert} service.
 */
@Service
public class AlertService {

    private AlertRepository alertRepository;
    private AlertMapper alertMapper;
    private UserService userService;

    public AlertService(AlertRepository alertRepository, AlertMapper alertMapper, UserService userService) {
        this.alertRepository = alertRepository;
        this.alertMapper = alertMapper;
        this.userService = userService;
    }

    /**
     * Validates and saves given alert for the currently logged user.
     *
     * @param alert to save
     * @return the saved alert
     */
    public AlertDto save(AlertDto alert) {
        validate(alert);
        Alert toSave = alertMapper.toEntity(alert);
        toSave.setUser(userService.getCurrentUser());
        return alertMapper.toDto(alertRepository.save(toSave));
    }

    private void validate(AlertDto alert) {
        alert.getMonitoredFields().forEach(field -> {
            if (field.getMax() == null && field.getMin() == null) {
                throw new BadRequestException("Monitored field '" + field.getField() + "' has its min and max values undefined");
            } else if (field.getMax() != null && field.getMin() != null && field.getMin().compareTo(field.getMax()) > 0) {
                throw new BadRequestException("Monitored field '" + field.getField() + "' has its min value greater than its max value: '["
                        + field.getMin() + ", " + field.getMax() + "]'");
            }
        });
        MonitoredDaysDto monitoredDays = alert.getMonitoredDays();
        if (Stream.of(monitoredDays.getNextDay(), monitoredDays.getSameDay(), monitoredDays.getTwoDayLater()).allMatch(BooleanUtils::isNotTrue)) {
            throw new BadRequestException("Given alert has no monitored days");
        }
    }

    /**
     * Finds all alerts for the currently logged user.
     *
     * @return
     */
    public List<AlertDto> findAllForCurrentUser() {
        return alertMapper.toDtoList(alertRepository.findByUserLogin(userService.getCurrentUser().getLogin()));
    }

    /**
     * Updates the given alert.
     *
     * @param alert new alert
     * @return the updated alert
     */
    public AlertDto update(AlertDto alert) {
        Optional.ofNullable(alert.getId()).flatMap(alertRepository::findById)
                .orElseThrow(() -> new BadRequestException("Alert to update with id '" + alert.getId() + "' is unknown"));
        return save(alert);
    }

}
