package guru.qa.niffler.tests.friends;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.models.UserJson;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import guru.qa.niffler.tests.BaseWebTest;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.User.UserType.WITH_FRIENDS;

public class WithBeforeEachParameterFriendsWebTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    FriendsPage friendsPage = new FriendsPage();

    @BeforeEach
    void login(@User(userType = WITH_FRIENDS) UserJson userForTest) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userForTest.getUsername())
                .setPassword(userForTest.getPassword())
                .successSubmit();
    }

    @Test
    @AllureId("106")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage() {
        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriend();
    }

}
