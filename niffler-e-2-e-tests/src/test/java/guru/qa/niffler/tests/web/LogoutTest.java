package guru.qa.niffler.tests.web;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LogoutTest extends BaseWebTest {

    @Test
    @DisplayName("WEB: После выхода с аккаунта открывается приветственная страница")
    @DBUser
    void welcomePageShouldBeVisibleAfterLogout(AuthUserEntity createdUser) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login()
                .setUsername(createdUser.getUsername())
                .setPassword(createdUser.getEncodedPassword())
                .successSubmit()
                .waitForPageIsLoaded()
                .getHeader()
                .logout()
                .waitForPageIsLoaded();
    }

}
