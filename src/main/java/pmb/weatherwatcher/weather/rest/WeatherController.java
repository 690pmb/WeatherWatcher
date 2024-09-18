package pmb.weatherwatcher.weather.rest;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pmb.weatherwatcher.weather.api.model.SearchJsonResponse;
import pmb.weatherwatcher.weather.dto.ForecastDto;
import pmb.weatherwatcher.weather.service.WeatherService;

@RestController
@RequestMapping(path = "/weathers")
public class WeatherController {

  private final WeatherService weatherService;

  public WeatherController(WeatherService weatherService) {
    this.weatherService = weatherService;
  }

  @GetMapping
  public ForecastDto findForecastByLocation(
      @RequestParam(required = false) String location,
      @RequestParam(required = false) Integer days,
      @RequestParam(required = false) String lang) {
    return weatherService.findForecastByLocation(location, days, lang);
  }

  @GetMapping("/locations")
  public List<SearchJsonResponse> searchLocations(@RequestParam String query) {
    return weatherService.searchLocations(query);
  }
}
