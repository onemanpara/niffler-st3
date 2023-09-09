package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(BrowserExtension.class)
@Inherited
public @interface WebTest {

    String browserSize() default "1920x1080";

}
