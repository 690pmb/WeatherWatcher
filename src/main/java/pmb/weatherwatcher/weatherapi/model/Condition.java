package pmb.weatherwatcher.weatherapi.model;

public class Condition
        implements java.io.Serializable {

    private static final long serialVersionUID = 330043717074438001L;
    private String text;
    private String icon;
    private Integer code;

    /**
     * GETTER Weather condition text
     */
    public String getText() {
        return text;
    }

    /**
     * SETTER Weather condition text
     */
    public void setText(String value) {
        text = value;
    }

    /**
     * GETTER Weather icon url
     */
    public String getIcon() {
        return icon;
    }

    /**
     * SETTER Weather icon url
     */
    public void setIcon(String value) {
        icon = value;
    }

    /**
     * GETTER Weather condition unique code.
     */
    public Integer getCode() {
        return code;
    }

    /**
     * SETTER Weather condition unique code.
     */
    public void setCode(Integer value) {
        code = value;
    }

}
