package guru.qa.niffler.tests.web.friends;

import guru.qa.niffler.jupiter.annotations.*;
import guru.qa.niffler.models.UserJson;
import guru.qa.niffler.tests.web.BaseWebTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FriendsWebTests extends BaseWebTest {

    @Test
    @DisplayName("WEB: Друзья пользователя отображаются в таблице на странице друзей")
    @ApiLogin(user = @GenerateUser(friends = @Friend(count = 2)))
    void friendShouldBeDisplayedInTableAtFriendsPage(@GeneratedUser UserJson userForTest) {
        mainPage
                .openPage()
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriend(userForTest.getFriends().get(0).getUsername())
                .checkUserHaveFriend(userForTest.getFriends().get(1).getUsername());
    }

    @Test
    @DisplayName("WEB: Отправленные приглашения в друзья отображаются в таблице на странице со всеми пользователями")
    @ApiLogin(user = @GenerateUser(outcomeInvitations = @OutcomeInvitation(count = 2)))
    void sentFriendInvitationShouldBeDisplayedInTableAtPeoplePage(@GeneratedUser UserJson createdUser) {
        mainPage
                .openPage()
                .waitForPageIsLoaded()
                .getHeader()
                .goToPeoplePage();

        peoplePage
                .waitForPageIsLoaded()
                .checkInvitationToFriendSent(createdUser.getOutcomeInvitations().get(0).getUsername())
                .checkInvitationToFriendSent(createdUser.getOutcomeInvitations().get(1).getUsername());
    }

    @Test
    @DisplayName("WEB: Полученные приглашения в друзья отображаются в таблице на странице друзей")
    @ApiLogin(user = @GenerateUser(incomeInvitations = @IncomeInvitation(count = 2)))
    void friendInvitationShouldBeDisplayedInTableAtFriendsPage(@GeneratedUser UserJson createdUser) {
        mainPage
                .openPage()
                .waitForPageIsLoaded()
                .getHeader()
                .goToFriendsPage();

        friendsPage
                .waitForPageIsLoaded()
                .checkUserHaveFriendInvitation(createdUser.getIncomeInvitations().get(0).getUsername())
                .checkUserHaveFriendInvitation(createdUser.getIncomeInvitations().get(1).getUsername());
    }

}
