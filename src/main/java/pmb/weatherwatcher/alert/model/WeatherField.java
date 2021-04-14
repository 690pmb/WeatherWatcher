package pmb.weatherwatcher.alert.model;

/**
 * Weather fields that can be monitored, the code is the field's name from the Weather API.
 */
public enum WeatherField {

    TEMP("tempC"),
    WIND("windKph"),
    PRESSURE("pressureMb"),
    PRECIP("precipMm"),
    HUMIDITY("humidity"),
    CLOUD("cloud"),
    FEELS_LIKE("feelsLikeC"),
    CHANCE_OF_RAIN("chanceOfRain"),
    CHANCE_OF_SNOW("chanceOfSnow"),
    UV("uv");

    private final String code;

    WeatherField(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
