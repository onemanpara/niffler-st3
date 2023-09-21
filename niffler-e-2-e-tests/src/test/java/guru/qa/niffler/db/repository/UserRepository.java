package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.auth.AuthUserEntity;

public interface UserRepository {

    void createUserForTest(AuthUserEntity user);

    void removeUser(AuthUserEntity user);

}
