package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.userdata.UserDataEntity;

public interface UserDataDAO {

    void createUserInUserData(UserDataEntity user);

    UserDataEntity getUserFromUserDataByUsername(String username);

    void updateUserInUserData(UserDataEntity user);

    void deleteUserInUserData(UserDataEntity user);
}
