package pmb.weatherwatcher.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Thrown when a resource already exist in database.
 */
public class NotFoundException
        extends NestedRuntimeException {

    private static final long serialVersionUID = -7633549396307574157L;

    public NotFoundException(String msg) {
        super(msg);
    }

}
