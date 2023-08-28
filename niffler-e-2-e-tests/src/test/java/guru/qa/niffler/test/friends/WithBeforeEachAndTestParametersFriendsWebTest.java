package guru.qa.niffler.test.friends;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import guru.qa.niffler.test.BaseWebTest;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WithBeforeEachAndTestParametersFriendsWebTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    FriendsPage friendsPage = new FriendsPage();

    @BeforeEach
    void login(@User(userType = User.UserType.INVITATION_RECEIVED) UserJson userForTest) {
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
    @AllureId("105")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage(@User(userType = User.UserType.INVITATION_SENT) UserJson userForTest) {
        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriendInvitation(userForTest.getUsername());
    }


}
