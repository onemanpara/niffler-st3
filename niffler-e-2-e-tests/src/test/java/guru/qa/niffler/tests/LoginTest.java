package guru.qa.niffler.tests;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();

    @DBUser(password = "12345")
    @Test
    void mainPageShouldBeVisibleAfterLogin(AuthUserEntity createdUser) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login()
                .setUsername(createdUser.getUsername())
                .setPassword("12345")
                .successSubmit()
                .waitForPageIsLoaded();
    }

}
