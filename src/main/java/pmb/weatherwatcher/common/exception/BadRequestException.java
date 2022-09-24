package pmb.weatherwatcher.common.exception;

import org.springframework.core.NestedRuntimeException;

/** Exception to throw when a bad request is made. */
public class BadRequestException extends NestedRuntimeException {

  private static final long serialVersionUID = -7633549396307574157L;

  public BadRequestException(String msg) {
    super(msg);
  }
}
