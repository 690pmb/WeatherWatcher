package pmb.weatherwatcher;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Target(TYPE)
@Retention(RUNTIME)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
public @interface ServiceTestRunner {}
