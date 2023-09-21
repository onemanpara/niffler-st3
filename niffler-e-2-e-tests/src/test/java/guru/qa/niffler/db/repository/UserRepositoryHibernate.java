package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.dao.impl.hibernate.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.hibernate.UserDataDAOHibernate;

public class UserRepositoryHibernate extends AbstractUserRepository {
    public UserRepositoryHibernate() {
        super(new AuthUserDAOHibernate(), new UserDataDAOHibernate());
    }
}
