package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.dao.impl.springjdbc.AuthUserDAOSpringJdbc;
import guru.qa.niffler.db.dao.impl.springjdbc.UserDataDAOSpringJdbc;

public class UserRepositorySpringJdbc extends AbstractUserRepository {
    public UserRepositorySpringJdbc() {
        super(new AuthUserDAOSpringJdbc(), new UserDataDAOSpringJdbc());
    }
}
