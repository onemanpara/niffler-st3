package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.model.auth.AuthUserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate implements AuthUserDAO {
    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        return null;
    }

    @Override
    public AuthUserEntity getUserFromAuthUserById(UUID userId) {
        return null;
    }

    @Override
    public void updateUser(AuthUserEntity user) {

    }

    @Override
    public void deleteUserByIdInAuth(UUID userId) {

    }
}
