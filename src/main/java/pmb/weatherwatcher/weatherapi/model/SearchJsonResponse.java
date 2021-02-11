package pmb.weatherwatcher.weatherapi.model;

public class SearchJsonResponse
        implements java.io.Serializable {

    private static final long serialVersionUID = -5188986890530896034L;
    private Integer id;
    private String name;
    private String region;
    private String country;
    private Double lat;
    private Double lon;
    private String url;

    /**
     * GETTER
     */
    public Integer getId() {
        return id;
    }

    /**
     * SETTER
     */
    public void setId(Integer value) {
        id = value;
    }

    /**
     * GETTER Local area name.
     */
    public String getName() {
        return name;
    }

    /**
     * SETTER Local area name.
     */
    public void setName(String value) {
        name = value;
    }

    /**
     * GETTER Local area region.
     */
    public String getRegion() {
        return region;
    }

    /**
     * SETTER Local area region.
     */
    public void setRegion(String value) {
        region = value;
    }

    /**
     * GETTER Country
     */
    public String getCountry() {
        return country;
    }

    /**
     * SETTER Country
     */
    public void setCountry(String value) {
        country = value;
    }

    /**
     * GETTER Area latitude
     */
    public Double getLat() {
        return lat;
    }

    /**
     * SETTER Area latitude
     */
    public void setLat(Double value) {
        lat = value;
    }

    /**
     * GETTER Area longitude
     */
    public Double getLon() {
        return lon;
    }

    /**
     * SETTER Area longitude
     */
    public void setLon(Double value) {
        lon = value;
    }

    /**
     * GETTER
     */
    public String getUrl() {
        return url;
    }

    /**
     * SETTER
     */
    public void setUrl(String value) {
        url = value;
    }

}
