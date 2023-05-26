package pmb.weatherwatcher.notification.rest;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pmb.weatherwatcher.notification.dto.SubscriptionDto;
import pmb.weatherwatcher.notification.service.SubscriptionService;

/** Subscription rest controller. */
@RestController
@RequestMapping(path = "/notifications/subscriptions")
public class SubscriptionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);

  private final SubscriptionService subscriptionService;

  public SubscriptionController(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  @PostMapping
  public SubscriptionDto post(@RequestBody @Valid SubscriptionDto subscription) {
    LOGGER.debug("creating subscription");
    return subscriptionService.save(subscription);
  }

  @DeleteMapping
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void delete(@RequestParam String userAgent) {
    LOGGER.debug("Deleting subscriptions");
    subscriptionService.deleteOtherByUserId(userAgent);
  }
}
