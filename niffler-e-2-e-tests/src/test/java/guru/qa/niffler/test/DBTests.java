package guru.qa.niffler.test;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBTests {

    AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    UserDataUserDAO userDataUserDAO = new AuthUserDAOJdbc();

    @DBUser(username = "anton", password = "12345")
    @Test
    void shouldReadUserFromAuthDB(UserEntity createdUser) {
        UserEntity user = authUserDAO.getUserFromAuthUserById(createdUser.getId());
        assertEquals("ivan", user.getUsername());
        assertEquals(true, user.getEnabled());
        assertEquals(true, user.getAccountNonLocked());
        assertEquals(true, user.getCredentialsNonExpired());
    }

    @DBUser(username = "maksim", password = "12345")
    @Test
    void shouldUpdateUserInAuthDB(UserEntity createdUser) {
        createdUser.setEnabled(false);
        createdUser.setAccountNonExpired(false);
        createdUser.setAccountNonLocked(false);
        createdUser.setCredentialsNonExpired(false);
        authUserDAO.updateUser(createdUser);

        UserEntity user = authUserDAO.getUserFromAuthUserById(createdUser.getId());
        assertEquals(false, user.getEnabled());
        assertEquals(false, user.getAccountNonExpired());
        assertEquals(false, user.getAccountNonLocked());
        assertEquals(false, user.getCredentialsNonExpired());
    }

    @DBUser(username = "ivan", password = "12345")
    @Test
    void shouldUpdateUserInUserDataDB(UserEntity createdUser) {
        UserDataEntity user = userDataUserDAO.getUserFromUserDataByUsername(createdUser.getUsername());
        user.setFirstname("Ivan");
        user.setSurname("Ivanov");
        user.setCurrency(CurrencyValues.KZT);
        userDataUserDAO.updateUserInUserData(user);

        user = userDataUserDAO.getUserFromUserDataByUsername(createdUser.getUsername());
        assertEquals("Ivan", user.getFirstname());
        assertEquals("Ivanov", user.getSurname());
        assertEquals(CurrencyValues.KZT, user.getCurrency());
    }
}
