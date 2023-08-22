package guru.qa.niffler.test.friends;


import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.*;
import guru.qa.niffler.test.BaseWebTest;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.User.UserType.*;

public class FriendsWebTest extends BaseWebTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    FriendsPage friendsPage = new FriendsPage();
    PeoplePage peoplePage = new PeoplePage();

    @Test
    @AllureId("101")
    void friendShouldBeDisplayedInTableAtFriendsPage(@User(userType = WITH_FRIENDS) UserJson userForTest) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userForTest.getUsername())
                .setPassword(userForTest.getPassword())
                .successSubmit();

        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriend();
    }

    @Test
    @AllureId("102")
    void sentFriendInvitationShouldBeDisplayedInTableAtPeoplePage(@User(userType = INVITATION_SENT) UserJson userForTest) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userForTest.getUsername())
                .setPassword(userForTest.getPassword())
                .successSubmit();

        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToPeoplePage();

        peoplePage
                .waitForPageIsLoaded()
                .checkInvitationToFriendSent();
    }

    @Test
    @AllureId("103")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage0(@User(userType = INVITATION_SENT) UserJson userWithInvitationSent,
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
    @AllureId("104")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage1(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login();

        loginPage
                .waitForPageIsLoaded()
                .setUsername(userForTest.getUsername())
                .setPassword(userForTest.getPassword())
                .successSubmit();

        mainPage
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriendInvitation();
    }

    @Disabled("Not working :(")
    @Test
    @AllureId("999")
    void testWithSameParameters(@User(userType = INVITATION_RECEIVED) UserJson firstUser,
                                @User(userType = INVITATION_RECEIVED) UserJson secondUser) {
        System.out.println(firstUser.getUsername());
        System.out.println(secondUser.getUsername());
    }

}
