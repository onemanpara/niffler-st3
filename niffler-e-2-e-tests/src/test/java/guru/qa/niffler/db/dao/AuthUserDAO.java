package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public interface AuthUserDAO {

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    AuthUserEntity createUser(AuthUserEntity user);

    AuthUserEntity getUserFromAuthUserById(UUID userId);

    void updateUser(AuthUserEntity user);

    void deleteUserByIdInAuth(UUID userId);
}
