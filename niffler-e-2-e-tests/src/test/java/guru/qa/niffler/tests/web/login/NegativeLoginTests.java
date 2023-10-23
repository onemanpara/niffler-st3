package guru.qa.niffler.tests.web.login;

import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.jupiter.annotations.GeneratedUser;
import guru.qa.niffler.models.UserJson;
import guru.qa.niffler.tests.web.BaseWebTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.GeneratedUser.Selector.OUTER;

public class NegativeLoginTests extends BaseWebTest {

    @Test
    @DisplayName("WEB: При авторизации возникает ошибка, если отправляется некорректный пароль пользователя")
    @GenerateUser
    void shouldShowErrorIfSendingIncorrectPassword(@GeneratedUser(selector = OUTER) UserJson createdUser) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login()
                .setUsername(createdUser.getUsername())
                .setPassword("1")
                .errorSubmit()
                .checkErrorMessage("Неверные учетные данные пользователя");
    }

}
