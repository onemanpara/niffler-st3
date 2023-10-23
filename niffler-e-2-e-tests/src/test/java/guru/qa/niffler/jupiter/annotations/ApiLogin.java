package guru.qa.niffler.jupiter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiLogin {
    String username() default "";

    String password() default "";

    GenerateUser user() default @GenerateUser(handleAnnotation = false);

}
