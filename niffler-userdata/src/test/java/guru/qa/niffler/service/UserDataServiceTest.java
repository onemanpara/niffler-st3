package guru.qa.niffler.service;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.model.FriendJson;
import guru.qa.niffler.model.FriendState;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static guru.qa.niffler.model.FriendState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceTest {

    private UserDataService testedObject;

    private final UserEntity mainTestUser;
    private final UserEntity secondTestUser;
    private final UserEntity thirdTestUser;
    private final String notExistingUser = "not_existing_user";

    public UserDataServiceTest() {
        mainTestUser = new UserEntity();
        mainTestUser.setId(UUID.randomUUID());
        mainTestUser.setUsername("misha");
        mainTestUser.setCurrency(CurrencyValues.RUB);

        secondTestUser = new UserEntity();
        secondTestUser.setId(UUID.randomUUID());
        secondTestUser.setUsername("barsik");
        secondTestUser.setCurrency(CurrencyValues.RUB);

        thirdTestUser = new UserEntity();
        thirdTestUser.setId(UUID.randomUUID());
        thirdTestUser.setUsername("emma");
        thirdTestUser.setCurrency(CurrencyValues.RUB);
    }

    @ValueSource(strings = {"photo", ""})
    @ParameterizedTest
    void userShouldBeUpdated(String photo, @Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUser.getUsername())))
                .thenReturn(mainTestUser);

        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(answer -> answer.getArguments()[0]);

        testedObject = new UserDataService(userRepository);

        final String photoForTest = photo.isEmpty() ? null : photo;

        final UserJson toBeUpdated = new UserJson();
        toBeUpdated.setUsername(mainTestUser.getUsername());
        toBeUpdated.setFirstname("Test");
        toBeUpdated.setSurname("TestSurname");
        toBeUpdated.setCurrency(CurrencyValues.USD);
        toBeUpdated.setPhoto(photoForTest);
        final UserJson result = testedObject.update(toBeUpdated);
        assertEquals(mainTestUser.getId(), result.getId());
        assertEquals("Test", result.getFirstname());
        assertEquals("TestSurname", result.getSurname());
        assertEquals(CurrencyValues.USD, result.getCurrency());
        assertEquals(photoForTest, result.getPhoto());

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void getRequiredUserShouldThrowNotFoundExceptionIfUserNotFound(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(notExistingUser)))
                .thenReturn(null);

        testedObject = new UserDataService(userRepository);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> testedObject.getRequiredUser(notExistingUser));
        assertEquals(
                "Can`t find user by username: " + notExistingUser,
                exception.getMessage()
        );
    }

    @Test
    void getRequiredUserShouldReturnUserIfFound(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUser.getUsername())))
                .thenReturn(mainTestUser);

        testedObject = new UserDataService(userRepository);

        final UserJson foundedUser = UserJson.fromEntity(testedObject.getRequiredUser(mainTestUser.getUsername()));

        assertEquals(mainTestUser.getId(), foundedUser.getId());
        assertEquals(mainTestUser.getUsername(), foundedUser.getUsername());
        assertEquals(CurrencyValues.RUB, foundedUser.getCurrency());
    }

    @Test
    void allUsersShouldReturnCorrectUsersList(@Mock UserRepository userRepository) {
        when(userRepository.findByUsernameNot(eq(mainTestUser.getUsername())))
                .thenReturn(getMockUsersMappingFromDb());

        testedObject = new UserDataService(userRepository);

        List<UserJson> users = testedObject.allUsers(mainTestUser.getUsername());
        assertEquals(2, users.size());
        final UserJson invitation = users.stream()
                .filter(u -> u.getFriendState() == INVITE_SENT)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Friend with state INVITE_SENT not found"));

        final UserJson friend = users.stream()
                .filter(u -> u.getFriendState() == FRIEND)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Friend with state FRIEND not found"));


        assertEquals(secondTestUser.getUsername(), invitation.getUsername());
        assertEquals(thirdTestUser.getUsername(), friend.getUsername());
    }


    static Stream<Arguments> friendsShouldReturnDifferentListsBasedOnIncludePendingParam() {
        return Stream.of(
                Arguments.of(true, List.of(INVITE_SENT, FRIEND)),
                Arguments.of(false, List.of(FRIEND))
        );
    }

    @MethodSource
    @ParameterizedTest
    void friendsShouldReturnDifferentListsBasedOnIncludePendingParam(boolean includePending,
                                                                     List<FriendState> expectedStates,
                                                                     @Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUser.getUsername())))
                .thenReturn(withSentInviteAndFriendTestUser());

        testedObject = new UserDataService(userRepository);

        final List<UserJson> result = testedObject.friends(mainTestUser.getUsername(), includePending);
        assertEquals(expectedStates.size(), result.size());

        assertTrue(result.stream()
                .map(UserJson::getFriendState)
                .toList()
                .containsAll(expectedStates));
    }

    @Test
    void invitationsShouldReturnListOfReceivedInvites(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(mainTestUser.getUsername()))
                .thenReturn(withReceivedInvitesTestUser());

        testedObject = new UserDataService(userRepository);

        final List<UserJson> invitations = testedObject.invitations(mainTestUser.getUsername());

        assertEquals(2, invitations.size());
        assertEquals(
                INVITE_RECEIVED,
                invitations.stream()
                        .filter(inv -> inv.getUsername().equals(secondTestUser.getUsername()))
                        .findFirst()
                        .get()
                        .getFriendState()
        );
    }

    @Test
    void addFriendShouldReturnUserWhomRequestWasSent(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(mainTestUser.getUsername()))
                .thenReturn(mainTestUser);
        when(userRepository.findByUsername(secondTestUser.getUsername()))
                .thenReturn(secondTestUser);

        FriendJson friendToAdd = new FriendJson();
        friendToAdd.setUsername(secondTestUser.getUsername());

        testedObject = new UserDataService(userRepository);

        final UserJson friendWhomAdded = testedObject.addFriend(mainTestUser.getUsername(), friendToAdd);
        assertEquals(INVITE_SENT, friendWhomAdded.getFriendState());
        assertEquals(secondTestUser.getUsername(), friendWhomAdded.getUsername());
    }

    @Test
    void acceptInvitationShouldRemovePropertyPendingFromAddingFriend(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(mainTestUser.getUsername()))
                .thenReturn(withReceivedInvitesTestUser());
        when(userRepository.findByUsername(secondTestUser.getUsername()))
                .thenReturn(secondTestUser);

        FriendJson invitationToAccept = new FriendJson();
        invitationToAccept.setUsername(secondTestUser.getUsername());

        testedObject = new UserDataService(userRepository);

        final List<UserJson> mainUsersFriends = testedObject.acceptInvitation(mainTestUser.getUsername(), invitationToAccept);
        final List<UserJson> onlyAcceptedFriends = mainUsersFriends.stream().filter(f -> f.getFriendState() == FRIEND).toList();
        assertEquals(1, onlyAcceptedFriends.size());
        assertEquals(secondTestUser.getUsername(), onlyAcceptedFriends.get(0).getUsername());

        final UserEntity acceptedFriend = userRepository.findByUsername(secondTestUser.getUsername());
        assertEquals(1, acceptedFriend.getFriends().size());
        assertEquals(mainTestUser.getUsername(), acceptedFriend.getFriends().get(0).getFriend().getUsername());
    }

    @Test
    void acceptInvitationShouldThrowExceptionIfUsernameToAcceptNotFound(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(mainTestUser.getUsername()))
                .thenReturn(withReceivedInvitesTestUser());
        when(userRepository.findByUsername(eq(notExistingUser)))
                .thenReturn(null);

        FriendJson invitationToAccept = new FriendJson();
        invitationToAccept.setUsername(notExistingUser);

        testedObject = new UserDataService(userRepository);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> testedObject.acceptInvitation(mainTestUser.getUsername(), invitationToAccept));
        assertEquals(
                "Can`t find user by username: " + notExistingUser,
                exception.getMessage()
        );
    }

    @Test
    void declineInvitationShouldReturnActualListOfReceivedInvites(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(mainTestUser.getUsername()))
                .thenReturn(withReceivedInvitesTestUser());
        when(userRepository.findByUsername(secondTestUser.getUsername()))
                .thenReturn(secondTestUser);

        FriendJson friendInvitation = new FriendJson();
        friendInvitation.setUsername(secondTestUser.getUsername());

        testedObject = new UserDataService(userRepository);

        final List<UserJson> invitations = testedObject.declineInvitation(mainTestUser.getUsername(), friendInvitation);
        assertEquals(1, invitations.size());

        final UserJson invitation = invitations.get(0);
        assertEquals(thirdTestUser.getUsername(), invitation.getUsername());
        assertEquals(INVITE_RECEIVED, invitation.getFriendState());

        final UserEntity declinedFriend = userRepository.findByUsername(secondTestUser.getUsername());
        assertEquals(0, declinedFriend.getFriends().size());
    }

    @Test
    void removeFriendShouldBreakFriendsTiesBetweenUsers(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(mainTestUser.getUsername()))
                .thenReturn(withSentInviteAndFriendTestUser());
        when(userRepository.findByUsername(thirdTestUser.getUsername()))
                .thenReturn(thirdTestUser);

        testedObject = new UserDataService(userRepository);

        final List<UserJson> actualFriendsList = testedObject.removeFriend(mainTestUser.getUsername(), thirdTestUser.getUsername());
        assertEquals(1, actualFriendsList.size());
        assertNotEquals(thirdTestUser.getUsername(), actualFriendsList.get(0).getUsername());
        assertNotEquals(FRIEND, actualFriendsList.get(0).getFriendState());

        final UserEntity removedFriend = userRepository.findByUsername(thirdTestUser.getUsername());
        assertEquals(0, removedFriend.getFriends().size());
    }

    @Test
    void removeFriendShouldBreakFriendPendingBetweenUsers(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(mainTestUser.getUsername()))
                .thenReturn(withSentInviteAndFriendTestUser());
        when(userRepository.findByUsername(secondTestUser.getUsername()))
                .thenReturn(secondTestUser);

        testedObject = new UserDataService(userRepository);

        final List<UserJson> actualFriendsList = testedObject.removeFriend(mainTestUser.getUsername(), secondTestUser.getUsername());
        assertEquals(1, actualFriendsList.size());
        assertNotEquals(secondTestUser.getUsername(), actualFriendsList.get(0).getUsername());
        assertNotEquals(INVITE_SENT, actualFriendsList.get(0).getFriendState());

        final UserEntity removedFriend = userRepository.findByUsername(secondTestUser.getUsername());
        assertEquals(0, removedFriend.getFriends().size());
    }

    private UserEntity withSentInviteAndFriendTestUser() {
        mainTestUser.addFriends(true, secondTestUser);
        secondTestUser.addInvites(mainTestUser);

        mainTestUser.addFriends(false, thirdTestUser);
        thirdTestUser.addFriends(false, mainTestUser);
        return mainTestUser;
    }

    private UserEntity withReceivedInvitesTestUser() {
        secondTestUser.addFriends(true, mainTestUser);
        mainTestUser.addInvites(secondTestUser);

        thirdTestUser.addFriends(true, mainTestUser);
        mainTestUser.addInvites(thirdTestUser);
        return mainTestUser;
    }


    private List<UserEntity> getMockUsersMappingFromDb() {
        mainTestUser.addFriends(true, secondTestUser);
        secondTestUser.addInvites(mainTestUser);

        mainTestUser.addFriends(false, thirdTestUser);
        thirdTestUser.addFriends(false, mainTestUser);

        return List.of(secondTestUser, thirdTestUser);
    }
}