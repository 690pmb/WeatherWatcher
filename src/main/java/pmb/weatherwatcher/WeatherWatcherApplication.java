package pmb.weatherwatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WeatherWatcherApplication {

  public static void main(String[] args) {
    new SpringApplication(WeatherWatcherApplication.class).run(args);
  }
}
