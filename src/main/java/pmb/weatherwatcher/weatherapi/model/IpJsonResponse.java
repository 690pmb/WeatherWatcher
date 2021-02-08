package pmb.weatherwatcher.weatherapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * location base from an IP address.
 */
public class IpJsonResponse
        implements java.io.Serializable {

    private static final long serialVersionUID = -6393844874178213400L;
    private String ip;
    private String type;
    @JsonProperty("continent_code")
    private String continentCode;
    @JsonProperty("continent_name")
    private String continentName;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("country_name")
    private String countryName;
    @JsonProperty("is_eu")
    private Boolean isEu;
    @JsonProperty("geoname_id")
    private Integer geoNameId;
    private String city;
    private String region;
    private Double lat;
    private Double lon;
    @JsonProperty("tz_id")
    private String tzId;
    @JsonProperty("localtime_epoch")
    private Integer localtimeEpoch;
    private String localtime;

    /**
     * GETTER IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * SETTER IP address
     */
    public void setIp(String value) {
        ip = value;
    }

    /**
     * GETTER ipv4 or ipv6
     */
    public String getType() {
        return type;
    }

    /**
     * SETTER ipv4 or ipv6
     */
    public void setType(String value) {
        type = value;
    }

    /**
     * GETTER Continent code
     */
    public String getContinentCode() {
        return continentCode;
    }

    /**
     * SETTER Continent code
     */
    public void setContinentCode(String value) {
        continentCode = value;
    }

    /**
     * GETTER Continent name
     */
    public String getContinentName() {
        return continentName;
    }

    /**
     * SETTER Continent name
     */
    public void setContinentName(String value) {
        continentName = value;
    }

    /**
     * GETTER Country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * SETTER Country code
     */
    public void setCountryCode(String value) {
        countryCode = value;
    }

    /**
     * GETTER Name of country
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * SETTER Name of country
     */
    public void setCountryName(String value) {
        countryName = value;
    }

    /**
     * GETTER true or false
     */
    public Boolean getIsEu() {
        return isEu;
    }

    /**
     * SETTER true or false
     */
    public void setIsEu(Boolean value) {
        isEu = value;
    }

    /**
     * GETTER Geoname ID
     */
    public Integer getGeoNameId() {
        return geoNameId;
    }

    /**
     * SETTER Geoname ID
     */
    public void setGeoNameId(Integer value) {
        geoNameId = value;
    }

    /**
     * GETTER City name
     */
    public String getCity() {
        return city;
    }

    /**
     * SETTER City name
     */
    public void setCity(String value) {
        city = value;
    }

    /**
     * GETTER Region name
     */
    public String getRegion() {
        return region;
    }

    /**
     * SETTER Region name
     */
    public void setRegion(String value) {
        region = value;
    }

    /**
     * GETTER Latitude in decimal degree
     */
    public Double getLat() {
        return lat;
    }

    /**
     * SETTER Latitude in decimal degree
     */
    public void setLat(Double value) {
        lat = value;
    }

    /**
     * GETTER Longitude in decimal degree
     */
    public Double getLon() {
        return lon;
    }

    /**
     * SETTER Longitude in decimal degree
     */
    public void setLon(Double value) {
        lon = value;
    }

    /**
     * GETTER Time zone
     */
    public String getTzId() {
        return tzId;
    }

    /**
     * SETTER Time zone
     */
    public void setTzId(String value) {
        tzId = value;
    }

    /**
     * GETTER Local time as epoch
     */
    public Integer getLocaltimeEpoch() {
        return localtimeEpoch;
    }

    /**
     * SETTER Local time as epoch
     */
    public void setLocaltimeEpoch(Integer value) {
        localtimeEpoch = value;
    }

    /**
     * GETTER Date and time
     */
    public String getLocaltime() {
        return localtime;
    }

    /**
     * SETTER Date and time
     */
    public void setLocaltime(String value) {
        localtime = value;
    }

}
