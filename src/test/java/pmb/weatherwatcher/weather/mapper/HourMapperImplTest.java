package pmb.weatherwatcher.weather.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pmb.weatherwatcher.weather.api.model.Direction;

class HourMapperImplTest {

  private HourMapperImpl hourMapperImpl = new HourMapperImpl();

  @EnumSource(Direction.class)
  @ParameterizedTest
  void roundedWind(Direction wind) {
    Direction rounded = hourMapperImpl.roundWind(wind);
    assertTrue(rounded.toString().length() < 3);
    assertTrue(rounded.toString().length() > 0);
    assertTrue(wind.toString().contains(rounded.toString()));
  }
}
