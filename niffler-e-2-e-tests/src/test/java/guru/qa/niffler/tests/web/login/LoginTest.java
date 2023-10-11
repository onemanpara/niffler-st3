package guru.qa.niffler.tests.web.login;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.tests.web.BaseWebTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseWebTest {

    @Test
    @DisplayName("WEB: Личный кабинет должен отображаться после логина новым юзером")
    @DBUser
    void mainPageShouldBeVisibleAfterLogin(AuthUserEntity createdUser) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login()
                .setUsername(createdUser.getUsername())
                .setPassword(createdUser.getEncodedPassword())
                .successSubmit()
                .waitForPageIsLoaded();
    }

}
