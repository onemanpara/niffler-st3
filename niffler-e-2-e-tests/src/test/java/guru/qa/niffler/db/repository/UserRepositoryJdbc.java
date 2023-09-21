package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.dao.impl.jdbc.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.UserDataDAOJdbc;

public class UserRepositoryJdbc extends AbstractUserRepository {
    public UserRepositoryJdbc() {
        super(new AuthUserDAOJdbc(), new UserDataDAOJdbc());
    }
}
