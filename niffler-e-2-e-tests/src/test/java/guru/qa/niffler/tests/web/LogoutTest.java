package guru.qa.niffler.tests.web;

import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LogoutTest extends BaseWebTest {

    @Test
    @DisplayName("WEB: После выхода с аккаунта открывается приветственная страница")
    @ApiLogin(user = @GenerateUser)
    void welcomePageShouldBeVisibleAfterLogout() {
        mainPage
                .openPage()
                .waitForPageIsLoaded()
                .getHeader()
                .logout()
                .waitForPageIsLoaded();
    }

}
