package pmb.weatherwatcher.common.exception;

import org.springframework.core.NestedRuntimeException;

/** Thrown when a resource is not found. */
public class NotFoundException extends NestedRuntimeException {

  private static final long serialVersionUID = -7633549396307574157L;

  public NotFoundException(String msg) {
    super(msg);
  }
}
