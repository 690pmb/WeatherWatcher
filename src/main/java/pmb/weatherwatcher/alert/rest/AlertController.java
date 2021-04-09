package pmb.weatherwatcher.alert.rest;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pmb.weatherwatcher.alert.dto.AlertDto;
import pmb.weatherwatcher.alert.service.AlertService;

/**
 * Alert rest controller.
 */
@RestController
@RequestMapping(path = "/alerts")
public class AlertController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertController.class);

    private AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public AlertDto post(@RequestBody @Valid AlertDto alert) {
        LOGGER.debug("creating alert");
        return alertService.save(alert);
    }

    @PutMapping
    public AlertDto put(@RequestBody @Valid AlertDto alert) {
        LOGGER.debug("updating alert");
        return alertService.update(alert);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        LOGGER.debug("delete alert {}", id);
        alertService.delete(id);
    }

    @GetMapping
    public List<AlertDto> getAllByUser() {
        LOGGER.debug("gets alert");
        return alertService.findAllForCurrentUser();
    }

}
