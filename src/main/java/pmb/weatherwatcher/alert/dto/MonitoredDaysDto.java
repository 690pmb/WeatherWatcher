package pmb.weatherwatcher.alert.dto;

/**
 * Tells which of the next three days are to be monitored.
 */
public class MonitoredDaysDto {

    private Boolean sameDay;
    private Boolean nextDay;
    private Boolean twoDayLater;

    public Boolean getSameDay() {
        return sameDay;
    }

    public void setSameDay(Boolean sameDay) {
        this.sameDay = sameDay;
    }

    public Boolean getNextDay() {
        return nextDay;
    }

    public void setNextDay(Boolean nextDay) {
        this.nextDay = nextDay;
    }

    public Boolean getTwoDayLater() {
        return twoDayLater;
    }

    public void setTwoDayLater(Boolean twoDayLater) {
        this.twoDayLater = twoDayLater;
    }

}
