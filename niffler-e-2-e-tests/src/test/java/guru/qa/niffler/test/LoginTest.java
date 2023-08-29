package guru.qa.niffler.test;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();

    @DBUser
    @Test
    void mainPageShouldBeVisibleAfterLogin(AuthUserEntity createdUser) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login()
                .setUsername(createdUser.getUsername())
                .setPassword(createdUser.getPassword())
                .successSubmit()
                .waitForPageIsLoaded();
    }

}
