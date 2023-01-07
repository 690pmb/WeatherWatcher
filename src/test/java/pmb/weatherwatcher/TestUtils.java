package pmb.weatherwatcher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;

@DisplayNameGeneration(value = ReplaceUnderscores.class)
public final class TestUtils {

  private TestUtils() {}

  public static Function<ResultActions, String> readResponse =
      result -> {
        try {
          return result.andReturn().getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      };

  public static OffsetTime buildOffsetTime(int hour, int minute) {
    return OffsetTime.of(LocalTime.of(hour, minute), ZoneOffset.UTC);
  }

  private boolean findClass(String className) {
    try {
      TestUtils.class.getClassLoader().loadClass(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  private Stream<ClassInfo> getTestClasses() throws IOException {
    ClassPath cp = ClassPath.from(ClassLoader.getSystemClassLoader());
    return cp.getTopLevelClassesRecursive(this.getClass().getPackageName()).stream()
        .filter(
            c ->
                !c.getName().equals(this.getClass().getName())
                    && StringUtils.endsWith(c.getName(), "Test"));
  }

  @Test
  void check_test_locations_and_names() throws IOException {
    List<ClassInfo> incorrectClass =
        getTestClasses()
            .filter(c -> !findClass(StringUtils.substringBeforeLast(c.getName(), "Test")))
            .collect(Collectors.toList());

    assertTrue(
        incorrectClass.isEmpty(),
        "Following class are not located in the same package than their test class: "
            + incorrectClass.stream().map(ClassInfo::getName).collect(Collectors.joining(",")));
  }

  @Test
  void test_must_not_use_SpringBootTest_annotation() throws IOException {
    List<Class<?>> incorrectClass =
        getTestClasses()
            .map(ClassInfo::load)
            .filter(test -> test.isAnnotationPresent(SpringBootTest.class))
            .collect(Collectors.toList());

    assertTrue(
        incorrectClass.isEmpty(),
        "Following test class are using the SpringBootTest annotation: "
            + incorrectClass.stream().map(Class::getName).collect(Collectors.joining(",")));
  }
}
