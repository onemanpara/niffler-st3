package guru.qa.niffler.tests;


import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.models.UserJson;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.User.UserType.*;

public class FriendsWebTests extends BaseWebTest {

    @Test
    @DisplayName("WEB: Друзья пользователя отображаются в таблице на странице друзей")
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
    @DisplayName("WEB: Отправленное приглашение в друзья отображается в таблице на странице со всеми пользователями")
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
    @DisplayName("WEB: Полученное приглашение в друзья отображается в таблице на странице друзей")
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage(@User(userType = INVITATION_SENT) UserJson userWithInvitationSent,
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

    @Disabled("Не удаляется пользователь после прохождения теста из niffler-userdata (т.к. не удаляется запись из таблицы friends)")
    @Test
    @DisplayName("WEB: Пользователь может отправить приглашение в друзья")
    @DBUser
    void shouldSendFriendInvitation(AuthUserEntity createdUser) {
        welcomePage
                .openPage()
                .waitForPageIsLoaded()
                .login()
                .waitForPageIsLoaded()
                .setUsername(createdUser.getUsername())
                .setPassword(createdUser.getEncodedPassword())
                .successSubmit()
                .waitForPageIsLoaded();

        peoplePage
                .openPage()
                .waitForPageIsLoaded()
                .sendFriendInvitation();
        peoplePage
                .openPage()
                .waitForPageIsLoaded()
                .checkInvitationToFriendSent();
    }

}
