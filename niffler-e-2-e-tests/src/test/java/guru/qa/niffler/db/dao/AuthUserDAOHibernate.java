package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate implements AuthUserDAO {
    @Override
    public UUID createUser(UserEntity user) {
        return null;
    }

    @Override
    public UserEntity getUserFromAuthUserById(UUID userId) {
        return null;
    }

    @Override
    public void updateUser(UserEntity user) {

    }

    @Override
    public void deleteUserByIdInAuth(UUID userId) {

    }
}
