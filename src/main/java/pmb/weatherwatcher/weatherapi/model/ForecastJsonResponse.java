package pmb.weatherwatcher.weatherapi.model;

/**
 * Contains current and forecast weather for a location.
 */
public class ForecastJsonResponse
        implements java.io.Serializable {

    private static final long serialVersionUID = 155850995944491322L;
    private Location location;
    private Current current;
    private Forecast forecast;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location value) {
        location = value;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current value) {
        current = value;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast value) {
        forecast = value;
    }

}
