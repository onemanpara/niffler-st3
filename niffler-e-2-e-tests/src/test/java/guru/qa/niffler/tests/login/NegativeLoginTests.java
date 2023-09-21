package guru.qa.niffler.tests.login;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.tests.BaseWebTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NegativeLoginTests extends BaseWebTest {

    @Test
    @DisplayName("WEB: При авторизации возникает ошибка, если отправляется некорректный пароль пользователя")
    @DBUser
    void shouldShowErrorIfSendingIncorrectPassword(AuthUserEntity createdUser) {
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
