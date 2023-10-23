package guru.qa.niffler.tests.web.login;

import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.jupiter.annotations.GeneratedUser;
import guru.qa.niffler.models.UserJson;
import guru.qa.niffler.tests.web.BaseWebTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.GeneratedUser.Selector.OUTER;

public class LoginTest extends BaseWebTest {

    @Test
    @DisplayName("WEB: Личный кабинет должен отображаться после логина новым юзером")
    @GenerateUser
    void mainPageShouldBeVisibleAfterLogin(@GeneratedUser(selector = OUTER) UserJson createdUser) {
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
