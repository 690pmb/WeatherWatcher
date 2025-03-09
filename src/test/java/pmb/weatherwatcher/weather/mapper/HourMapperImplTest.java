package pmb.weatherwatcher.weather.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pmb.weatherwatcher.weather.api.model.Direction;
import pmb.weatherwatcher.weather.api.model.Hour;
import pmb.weatherwatcher.weather.dto.HourDto;

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

  @Test
  void timeEpoch() {
    Hour h = new Hour();
    h.setTimeEpoch(1740764225);
    HourDto dto = hourMapperImpl.toDto(h);
    ZonedDateTime zdt = dto.getZonedDateTime();

    assertAll(
        () -> assertNotNull(dto),
        () -> assertNotNull(zdt),
        () -> assertEquals(0, zdt.getOffset().getTotalSeconds()),
        () -> assertEquals("Z", zdt.getZone().getId()),
        () -> assertEquals(2025, zdt.getYear()),
        () -> assertEquals(2, zdt.getMonth().getValue()),
        () -> assertEquals(28, zdt.getDayOfMonth()),
        () -> assertEquals(17, zdt.getHour()),
        () -> assertEquals(37, zdt.getMinute()),
        () -> assertEquals(5, zdt.getSecond()));
  }
}
