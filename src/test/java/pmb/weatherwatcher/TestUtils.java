package pmb.weatherwatcher;

import java.io.UnsupportedEncodingException;
import java.util.function.Function;

import org.springframework.test.web.servlet.ResultActions;

public final class TestUtils {

    private TestUtils() {}

    public static Function<ResultActions, String> readResponse = result -> {
        try {
            return result.andReturn().getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    };

}
