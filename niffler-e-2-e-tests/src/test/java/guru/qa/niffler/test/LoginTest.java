package guru.qa.niffler.test;

import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();

    @BeforeEach
    @DBUser(username = "valentin_5", password = "12345")
    void beforeEachTest(UserEntity createdUser) {
        assertEquals("valentin_5", createdUser.getUsername());
    }

    @DBUser(username = "valentin_2", password = "12345")
    @Test
    void mainPageShouldBeVisibleAfterLogin(UserEntity createdUser) {
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
