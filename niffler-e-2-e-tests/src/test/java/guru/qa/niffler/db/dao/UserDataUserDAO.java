package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

public interface UserDataUserDAO {

    UserDataEntity createUserInUserData(UserEntity user);

    UserDataEntity getUserFromUserDataByUsername(String username);

    void updateUserInUserData(UserDataEntity user);

    void deleteUserByUsernameInUserData(String username);
}
