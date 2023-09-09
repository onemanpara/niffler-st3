package guru.qa.niffler.tests;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegistrationTest extends BaseWebTest {

    @Test
    @DisplayName("WEB: Пользователь может зарегистрироваться в системе")
    void shouldRegisterNewUser() {
        final Faker faker = new Faker();
        final String username = faker.name().username();
        final String password = faker.internet().password(3, 12);
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .register()
                .waitForPageIsLoaded()
                .setUsername(username)
                .setPassword(password)
                .setConfirmPassword(password)
                .submit()
                .checkSuccessRegisterMessageIsVisible("Congratulations! You've registered!")
                .login()
                .waitForPageIsLoaded()
                .setUsername(username)
                .setPassword(password)
                .successSubmit()
                .waitForPageIsLoaded();
    }
}
