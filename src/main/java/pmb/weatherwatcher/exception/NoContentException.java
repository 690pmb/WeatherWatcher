package pmb.weatherwatcher.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Thrown when no content returned.
 */
public class NoContentException
        extends NestedRuntimeException {

    private static final long serialVersionUID = -7633549396307574157L;

    public NoContentException(String msg) {
        super(msg);
    }

}
