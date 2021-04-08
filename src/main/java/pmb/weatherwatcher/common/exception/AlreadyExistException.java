package pmb.weatherwatcher.common.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Thrown when a resource already exist in database.
 */
public class AlreadyExistException
        extends NestedRuntimeException {

    private static final long serialVersionUID = -7633549396307574157L;

    public AlreadyExistException(String msg) {
        super(msg);
    }

}
