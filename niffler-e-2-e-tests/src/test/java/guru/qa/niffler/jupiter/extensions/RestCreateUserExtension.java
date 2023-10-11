package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.auth.AuthServiceClient;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.api.friend.FriendServiceClient;
import guru.qa.niffler.api.register.RegisterServiceClient;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.models.UserJson;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestCreateUserExtension extends CreateUserExtension {

    private final RegisterServiceClient registerService = new RegisterServiceClient();
    private final FriendServiceClient friendService = new FriendServiceClient();
    private final AuthServiceClient authService = new AuthServiceClient();

    @Override
    protected UserJson createUserForTest(GenerateUser annotation) throws IOException {
        Faker faker = new Faker();
        final String password = annotation.password().isEmpty() ? faker.internet().password(3, 12) : annotation.password();
        final String username = annotation.username().isEmpty() ? faker.name().username() : annotation.username();
        UserJson user = createUserJson(username, password);
        registerService.registerUser(username, password);
        return user;
    }

    @Override
    protected List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser) throws IOException {
        if (annotation.friends().handleAnnotation()) {
            List<UserJson> friendsList = new ArrayList<>();
            for (int i = 0; i < annotation.friends().count(); i++) {
                UserJson friend = createUserJson(null, null);
                registerService.registerUser(friend.getUsername(), friend.getPassword());
                addInvitation(currentUser.getUsername(), friend);
                acceptInvitation(currentUser, friend.getUsername());
                friendsList.add(friend);
            }
            return friendsList;
        } else return Collections.emptyList();
    }

    @Override
    protected List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) throws IOException {
        if (annotation.incomeInvitations().handleAnnotation()) {
            List<UserJson> friendsList = new ArrayList<>();
            for (int i = 0; i < annotation.incomeInvitations().count(); i++) {
                UserJson friend = createUserJson(null, null);
                registerService.registerUser(friend.getUsername(), friend.getPassword());
                addInvitation(currentUser.getUsername(), friend);
                friendsList.add(friend);
            }
            return friendsList;
        } else return Collections.emptyList();
    }

    @Override
    protected List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) throws IOException {
        if (annotation.outcomeInvitations().handleAnnotation()) {
            List<UserJson> friendsList = new ArrayList<>();
            for (int i = 0; i < annotation.outcomeInvitations().count(); i++) {
                UserJson friend = createUserJson(null, null);
                registerService.registerUser(friend.getUsername(), friend.getPassword());
                addInvitation(friend.getUsername(), currentUser);
                friendsList.add(friend);
            }
            return friendsList;
        } else return Collections.emptyList();
    }

    private UserJson createUserJson(@Nullable String desiredUsername, @Nullable String desiredPassword) {
        Faker faker = new Faker();
        final String username = desiredUsername == null ? faker.name().username() : desiredUsername;
        final String password = desiredPassword == null ? faker.internet().password(3, 12) : desiredPassword;
        UserJson user = new UserJson();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    private void addInvitation(String userWhoReceivedInvitation, UserJson userWhoSentInvitation) throws IOException {
        UserJson userWhoReceivedInvitationJson = new UserJson();
        userWhoReceivedInvitationJson.setUsername(userWhoReceivedInvitation);

        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        CookieContext cookieContext = CookieContext.getInstance();
        authService.doLogin(userWhoSentInvitation.getUsername(), userWhoSentInvitation.getPassword());


        friendService.addFriend(sessionStorageContext.getToken(), cookieContext.getXsrfTokenCookieValue(), userWhoReceivedInvitationJson);
    }

    private void acceptInvitation(UserJson userWhoReceivedInvitation, String userWhoSentInvitation) throws IOException {
        UserJson userWhoSentInvitationJson = new UserJson();
        userWhoSentInvitationJson.setUsername(userWhoSentInvitation);

        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        CookieContext cookieContext = CookieContext.getInstance();
        authService.doLogin(userWhoReceivedInvitation.getUsername(), userWhoReceivedInvitation.getPassword());

        friendService.acceptInvitation(sessionStorageContext.getToken(), cookieContext.getJSessionIdFormattedCookie(), userWhoSentInvitationJson);
    }
}
