package pmb.weatherwatcher.alert.rest;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
  @PageableAsQueryParam
  public Page<AlertDto> getAllByUser(
      @ParameterObject @PageableDefault(sort = "location", direction = Direction.ASC)
          Pageable pageable) {
    LOGGER.debug("gets alert");
    return alertService.findAllForCurrentUser(pageable);
  }

  @GetMapping("/{id}")
  public AlertDto getById(@PathVariable Long id) {
    LOGGER.debug("get alert with id: {}", id);
    return alertService.findById(id);
  }
}
