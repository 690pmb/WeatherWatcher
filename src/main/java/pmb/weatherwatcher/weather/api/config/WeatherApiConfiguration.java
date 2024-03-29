package pmb.weatherwatcher.weather.api.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WeatherApiConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).build();
  }
}
