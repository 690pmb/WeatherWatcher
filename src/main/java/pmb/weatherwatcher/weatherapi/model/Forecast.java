package pmb.weatherwatcher.weatherapi.model;

import java.util.List;

public class Forecast
        implements java.io.Serializable {

    private static final long serialVersionUID = -88144496279653692L;
    private List<Forecastday> forecastday;

    public List<Forecastday> getForecastday() {
        return forecastday;
    }

    public void setForecastday(List<Forecastday> value) {
        forecastday = value;
    }

}
