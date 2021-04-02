package pmb.weatherwatcher.rest;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pmb.weatherwatcher.dto.alert.AlertDto;
import pmb.weatherwatcher.service.AlertService;

/**
 * Alert rest controller.
 */
@RestController
@RequestMapping(path = "/alerts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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

}
