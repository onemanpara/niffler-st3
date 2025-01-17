package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataDAO;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

public abstract class AbstractUserRepository implements UserRepository {
    private final AuthUserDAO authUserDAO;
    private final UserDataDAO userDataDAO;

    protected AbstractUserRepository(AuthUserDAO authUserDAO, UserDataDAO userDataDAO) {
        this.authUserDAO = authUserDAO;
        this.userDataDAO = userDataDAO;
    }

    @Override
    public void createUserForTest(AuthUserEntity user) {
        authUserDAO.createUser(user);
        userDataDAO.createUserInUserData(fromAuthUser(user));
    }

    @Override
    public void removeUser(AuthUserEntity user) {
        UserDataEntity userInUserData = userDataDAO.getUserFromUserDataByUsername(user.getUsername());
        userDataDAO.deleteUserInUserData(userInUserData);
        authUserDAO.deleteUser(user);
    }

    @Override
    public void makeUsersFriends(UserDataEntity firstUser, UserDataEntity secondUser) {
        firstUser.addFriends(false, secondUser);
        secondUser.addFriends(false, firstUser);
        userDataDAO.updateUserInUserData(firstUser);
        userDataDAO.updateUserInUserData(secondUser);
    }

    @Override
    public void addInvitation(UserDataEntity userWhoReceivedInvitation, UserDataEntity userWhoSentInvitation) {
        userWhoReceivedInvitation.addFriends(true, userWhoSentInvitation);
        userDataDAO.updateUserInUserData(userWhoReceivedInvitation);
    }

    private UserDataEntity fromAuthUser(AuthUserEntity user) {
        UserDataEntity userData = new UserDataEntity();
        userData.setUsername(user.getUsername());
        userData.setCurrency(CurrencyValues.RUB);
        return userData;
    }
}
