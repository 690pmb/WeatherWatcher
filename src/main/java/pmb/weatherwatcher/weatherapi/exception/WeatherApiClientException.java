package pmb.weatherwatcher.weatherapi.exception;

public class WeatherApiClientException
		extends RuntimeException {

	private static final long serialVersionUID = -7742562891975447644L;

	public WeatherApiClientException() {
		super();
	}

	public WeatherApiClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public WeatherApiClientException(String message) {
		super(message);
	}

}
