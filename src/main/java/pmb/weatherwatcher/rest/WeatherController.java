package pmb.weatherwatcher.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pmb.weatherwatcher.dto.weather.ForecastDto;
import pmb.weatherwatcher.dto.weather.IpDto;
import pmb.weatherwatcher.service.WeatherService;
import pmb.weatherwatcher.weatherapi.model.SearchJsonResponse;

@RestController
@RequestMapping(path = "/weathers")
public class WeatherController {

    private WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ForecastDto findForecastbyLocation(@RequestParam(required = false) String location, @RequestParam(required = false) Integer days,
            @RequestParam(required = false) String lang, HttpServletRequest httpServletRequest) {
        return weatherService.findForecastbyLocation(location, days, lang, httpServletRequest.getRemoteAddr());
    }

    @GetMapping("/ip")
    public IpDto findLocationByIp(HttpServletRequest httpServletRequest) {
        return weatherService.findLocationByIp(httpServletRequest.getRemoteAddr());
    }

    @GetMapping("/locations")
    public List<SearchJsonResponse> searchLocations(@RequestParam String query) {
        return weatherService.searchLocations(query);
    }

}
