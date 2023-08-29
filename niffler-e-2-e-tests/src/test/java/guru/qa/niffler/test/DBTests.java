package guru.qa.niffler.test;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataDAO;
import guru.qa.niffler.db.dao.impl.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.db.dao.impl.UserDataDAOJdbc;
import guru.qa.niffler.db.dao.impl.UserDataDAOSpringJdbc;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBTests {

    AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    UserDataDAO userDataDAO = new UserDataDAOJdbc();

    UserDataDAO userDataDAOJdbc = new UserDataDAOSpringJdbc();

    AuthUserDAO authUserDAOJdbc = new AuthUserDAOSpringJdbc();

    @DBUser
    @Test
    void shouldUpdateUserInAuthDB(AuthUserEntity createdUser) {
        createdUser.setEnabled(false);
        createdUser.setAccountNonExpired(false);
        createdUser.setAccountNonLocked(false);
        createdUser.setCredentialsNonExpired(false);
        authUserDAO.updateUser(createdUser);

        AuthUserEntity user = authUserDAO.getUserFromAuthUserById(createdUser.getId());
        assertEquals(false, user.getEnabled());
        assertEquals(false, user.getAccountNonExpired());
        assertEquals(false, user.getAccountNonLocked());
        assertEquals(false, user.getCredentialsNonExpired());
    }

    @DBUser
    @Test
    void shouldUpdateUserInUserDataDB(AuthUserEntity createdUser) {
        UserDataEntity user = userDataDAO.getUserFromUserDataByUsername(createdUser.getUsername());
        user.setFirstname("Ivan");
        user.setSurname("Ivanov");
        user.setCurrency(CurrencyValues.KZT);
        userDataDAO.updateUserInUserData(user);

        user = userDataDAO.getUserFromUserDataByUsername(createdUser.getUsername());
        assertEquals("Ivan", user.getFirstname());
        assertEquals("Ivanov", user.getSurname());
        assertEquals(CurrencyValues.KZT, user.getCurrency());
    }

    @Test
    void shouldUpdateUserInAuthDBWithSpringJdbc() {
        //Arrange
        AuthUserEntity user = new AuthUserEntity();
        user.setUsername("valentin_spring");
        user.setPassword("12345");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }).toList());
        user = authUserDAOJdbc.createUser(user);

        //Act
        user.setEnabled(false);
        user.setAccountNonExpired(false);
        user.setAccountNonLocked(false);
        user.setCredentialsNonExpired(false);
        authUserDAOJdbc.updateUser(user);

        user = authUserDAOJdbc.getUserFromAuthUserById(user.getId());

        //Assert
        assertEquals(false, user.getEnabled());
        assertEquals(false, user.getAccountNonExpired());
        assertEquals(false, user.getAccountNonLocked());
        assertEquals(false, user.getCredentialsNonExpired());

        //Delete user
        authUserDAOJdbc.deleteUserByIdInAuth(user.getId());
    }

    @Test
    void shouldUpdateUserInUserDataDBWithSpringJdbc() {
        //Arrange
        AuthUserEntity user = new AuthUserEntity();
        user.setUsername("valentin_spring_2");
        user.setPassword("12345");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }).toList());
        user = authUserDAOJdbc.createUser(user);
        userDataDAOJdbc.createUserInUserData(user);

        //Act
        UserDataEntity userData = userDataDAOJdbc.getUserFromUserDataByUsername(user.getUsername());
        userData.setFirstname("Ivan");
        userData.setSurname("Ivanov");
        userData.setCurrency(CurrencyValues.KZT);
        userDataDAOJdbc.updateUserInUserData(userData);

        //Assert
        userData = userDataDAOJdbc.getUserFromUserDataByUsername(user.getUsername());
        assertEquals("Ivan", userData.getFirstname());
        assertEquals("Ivanov", userData.getSurname());
        assertEquals(CurrencyValues.KZT, userData.getCurrency());

        //Delete user
        authUserDAOJdbc.deleteUserByIdInAuth(user.getId());
        userDataDAOJdbc.deleteUserByUsernameInUserData(user.getUsername());
    }
}
