package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.jupiter.extensions.DBCreateUserExtension;
import guru.qa.niffler.jupiter.extensions.JpaExtension;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({AllureJunit5.class, DBCreateUserExtension.class, ApiLoginExtension.class, BrowserExtension.class, JpaExtension.class})
@Inherited
public @interface WebTest {

    String browserSize() default "1920x1080";

}
