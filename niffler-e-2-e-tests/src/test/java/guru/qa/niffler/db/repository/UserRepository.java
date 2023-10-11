package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

public interface UserRepository {

    void createUserForTest(AuthUserEntity user);

    void removeUser(AuthUserEntity user);

    void makeUsersFriends(UserDataEntity firstUser, UserDataEntity secondUser);

    void addInvitation(UserDataEntity userWhoReceivedInvitation, UserDataEntity userWhoSentInvitation);

}
