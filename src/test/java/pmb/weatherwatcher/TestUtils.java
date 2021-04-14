package pmb.weatherwatcher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public final class TestUtils {

    private TestUtils() {}

    public static Function<ResultActions, String> readResponse = result -> {
        try {
            return result.andReturn().getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    };

    private boolean findClass(String className) {
        try {
            TestUtils.class.getClassLoader().loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Test
    void checkClassesName() throws IOException {
        ClassPath cp = ClassPath.from(ClassLoader.getSystemClassLoader());
        List<ClassInfo> incorrectClass = cp.getTopLevelClassesRecursive(this.getClass().getPackageName()).stream()
                .filter(c -> !c.getName().equals(this.getClass().getName()) && StringUtils.endsWith(c.getName(), "Test"))
                .filter(c -> !findClass(StringUtils.substringBeforeLast(c.getName(), "Test"))).collect(Collectors.toList());

        assertTrue(incorrectClass.isEmpty(), incorrectClass.stream().map(ClassInfo::getName).collect(Collectors.joining(",")));
    }

}
