package pmb.weatherwatcher.alert.rest;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.service.AlertService;

/** Alert rest controller. */
@RestController
@RequestMapping(path = "/alerts")
public class AlertController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertController.class);

  private final AlertService alertService;

  public AlertController(AlertService alertService) {
    this.alertService = alertService;
  }

  @PostMapping
  public AlertDto post(@RequestBody @Valid AlertDto alert) {
    LOGGER.debug("creating alert");
    return alertService.create(alert);
  }

  @PutMapping
  public AlertDto put(@RequestBody @Valid AlertDto alert) {
    LOGGER.debug("updating alert");
    return alertService.update(alert);
  }

  @DeleteMapping
  public void delete(@RequestParam List<Long> ids) {
    LOGGER.debug("delete alerts {}", ids);
    alertService.delete(ids);
  }

  @GetMapping
  public List<AlertDto> getAllByUser() {
    LOGGER.debug("gets alert");
    return alertService.findAllForCurrentUser();
  }

  @GetMapping("/{id}")
  public AlertDto getById(@PathVariable Long id) {
    LOGGER.debug("get alert with id: {}", id);
    return alertService.findById(id);
  }
}
