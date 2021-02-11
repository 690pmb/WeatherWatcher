package pmb.weatherwatcher.weatherapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Astro
        implements java.io.Serializable {

    private static final long serialVersionUID = 8217103204793016969L;
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    @JsonProperty("moon_phase")
    private String moonPhase;
    @JsonProperty("moon_illumination")
    private Integer moonIllumination;

    /**
     * GETTER Sunrise time
     */
    public String getSunrise() {
        return sunrise;
    }

    /**
     * SETTER Sunrise time
     */
    public void setSunrise(String value) {
        sunrise = value;
    }

    /**
     * GETTER Sunset time
     */
    public String getSunset() {
        return sunset;
    }

    /**
     * SETTER Sunset time
     */
    public void setSunset(String value) {
        sunset = value;
    }

    /**
     * GETTER Moonrise time
     */
    public String getMoonrise() {
        return moonrise;
    }

    /**
     * SETTER Moonrise time
     */
    public void setMoonrise(String value) {
        moonrise = value;
    }

    /**
     * GETTER Moonset time
     */
    public String getMoonset() {
        return moonset;
    }

    /**
     * SETTER Moonset time
     */
    public void setMoonset(String value) {
        moonset = value;
    }

    public String getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(String moonPhase) {
        this.moonPhase = moonPhase;
    }

    public Integer getMoonIllumination() {
        return moonIllumination;
    }

    public void setMoonIllumination(Integer moonIllumination) {
        this.moonIllumination = moonIllumination;
    }

}
