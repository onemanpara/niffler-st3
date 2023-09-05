package guru.qa.niffler.tests;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataDAO;
import guru.qa.niffler.db.dao.impl.hibernate.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.hibernate.UserDataDAOHibernate;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBTests {

    UserDataDAO userDataDAO = new UserDataDAOHibernate();
    AuthUserDAO authUserDAO = new AuthUserDAOHibernate();

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

}
