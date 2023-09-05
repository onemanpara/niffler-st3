package guru.qa.niffler.tests.friends;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.models.UserJson;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import guru.qa.niffler.tests.BaseWebTest;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.User.UserType.INVITATION_RECEIVED;
import static guru.qa.niffler.jupiter.annotations.User.UserType.INVITATION_SENT;

public class UsersQueueTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    FriendsPage friendsPage = new FriendsPage();

    @Test
    @AllureId("0101")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage1(@User(userType = INVITATION_SENT) UserJson userWithInvitationSent,
                                                                @User(userType = INVITATION_RECEIVED) UserJson userWithInvitationRc) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userWithInvitationRc.getUsername())
                .setPassword(userWithInvitationRc.getPassword())
                .successSubmit();

        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriendInvitation(userWithInvitationSent.getUsername());
    }

    @Test
    @AllureId("0102")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage2(@User(userType = INVITATION_SENT) UserJson userWithInvitationSent,
                                                                @User(userType = INVITATION_RECEIVED) UserJson userWithInvitationRc) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userWithInvitationRc.getUsername())
                .setPassword(userWithInvitationRc.getPassword())
                .successSubmit();

        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriendInvitation(userWithInvitationSent.getUsername());
    }

    @Test
    @AllureId("0103")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage3(@User(userType = INVITATION_SENT) UserJson userWithInvitationSent,
                                                                @User(userType = INVITATION_RECEIVED) UserJson userWithInvitationRc) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userWithInvitationRc.getUsername())
                .setPassword(userWithInvitationRc.getPassword())
                .successSubmit();

        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriendInvitation(userWithInvitationSent.getUsername());
    }

    @Test
    @AllureId("0104")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage4(@User(userType = INVITATION_SENT) UserJson userWithInvitationSent,
                                                                @User(userType = INVITATION_RECEIVED) UserJson userWithInvitationRc) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userWithInvitationRc.getUsername())
                .setPassword(userWithInvitationRc.getPassword())
                .successSubmit();

        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriendInvitation(userWithInvitationSent.getUsername());
    }

}
