package pmb.weatherwatcher.common.exception;

import org.springframework.core.NestedRuntimeException;

/** When a process goes wrong when it really shouldn't. */
public class InternalServerErrorException extends NestedRuntimeException {

  private static final long serialVersionUID = -7633549396307574157L;

  public InternalServerErrorException(String msg) {
    super(msg);
  }
}
