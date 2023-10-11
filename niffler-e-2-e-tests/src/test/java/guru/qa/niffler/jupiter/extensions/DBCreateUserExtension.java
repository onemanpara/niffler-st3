package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.impl.hibernate.UserDataDAOHibernate;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.models.UserJson;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DBCreateUserExtension extends CreateUserExtension {
    @Override
    protected UserJson createUserForTest(GenerateUser annotation) {
        UserRepository userRepository = new UserRepositoryHibernate();
        Faker faker = new Faker();
        final String password = annotation.password().isEmpty() ? faker.internet().password(3, 12) : annotation.password();
        final String username = annotation.username().isEmpty() ? faker.name().username() : annotation.username();
        AuthUserEntity user = fillAuthUserEntity(username, password);
        userRepository.createUserForTest(user);
        return UserJson.fromEntity(user);
    }

    @Override
    protected List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser) {
        if (annotation.friends().handleAnnotation()) {
            List<UserJson> friendsList = new ArrayList<>();
            UserRepository userRepository = new UserRepositoryHibernate();
            UserDataDAOHibernate userDataDAOHibernate = new UserDataDAOHibernate();

            for (int i = 0; i < annotation.friends().count(); i++) {
                AuthUserEntity friendAuthData = fillAuthUserEntity(null, null);
                userRepository.createUserForTest(friendAuthData);
                UserDataEntity friendUserData = userDataDAOHibernate.getUserFromUserDataByUsername(friendAuthData.getUsername());

                UserDataEntity mainUserData = userDataDAOHibernate.getUserFromUserDataByUsername(currentUser.getUsername());
                userRepository.makeUsersFriends(mainUserData, friendUserData);
                friendsList.add(UserJson.fromEntity(friendAuthData));
            }
            return friendsList;
        } else return Collections.emptyList();
    }

    @Override
    protected List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        if (annotation.incomeInvitations().handleAnnotation()) {
            List<UserJson> incomeInvitations = new ArrayList<>();
            UserRepository userRepository = new UserRepositoryHibernate();
            UserDataDAOHibernate userDataDAOHibernate = new UserDataDAOHibernate();

            for (int i = 0; i < annotation.incomeInvitations().count(); i++) {
                AuthUserEntity friendAuthData = fillAuthUserEntity(null, null);
                userRepository.createUserForTest(friendAuthData);
                UserDataEntity friendUserData = userDataDAOHibernate.getUserFromUserDataByUsername(friendAuthData.getUsername());

                UserDataEntity currentUserData = userDataDAOHibernate.getUserFromUserDataByUsername(currentUser.getUsername());
                userRepository.addInvitation(friendUserData, currentUserData);
                incomeInvitations.add(UserJson.fromEntity(friendAuthData));
            }
            return incomeInvitations;
        } else return Collections.emptyList();
    }

    @Override
    protected List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser) {
        if (annotation.outcomeInvitations().handleAnnotation()) {
            List<UserJson> outcomeInvitations = new ArrayList<>();
            UserRepository userRepository = new UserRepositoryHibernate();
            UserDataDAOHibernate userDataDAOHibernate = new UserDataDAOHibernate();

            for (int i = 0; i < annotation.outcomeInvitations().count(); i++) {
                AuthUserEntity friendAuthData = fillAuthUserEntity(null, null);
                userRepository.createUserForTest(friendAuthData);
                UserDataEntity friendUserData = userDataDAOHibernate.getUserFromUserDataByUsername(friendAuthData.getUsername());

                UserDataEntity currentUserData = userDataDAOHibernate.getUserFromUserDataByUsername(currentUser.getUsername());
                userRepository.addInvitation(currentUserData, friendUserData);
                outcomeInvitations.add(UserJson.fromEntity(friendAuthData));
            }
            return outcomeInvitations;
        } else return Collections.emptyList();
    }

    private AuthUserEntity fillAuthUserEntity(@Nullable String desiredUsername, @Nullable String desiredPassword) {
        Faker faker = new Faker();
        AuthUserEntity user = new AuthUserEntity();

        final String username = desiredUsername == null ? faker.name().username() : desiredUsername;
        final String password = desiredPassword == null ? faker.internet().password(3, 12) : desiredPassword;

        user.setUsername(username);
        user.setPassword(password);
        user.setEncodedPassword(password);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(new ArrayList<>(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(user);
                    return ae;
                }).toList()));
        return user;
    }
}
