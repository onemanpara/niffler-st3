package guru.qa.niffler.db.dao.impl.hibernate;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.auth.AuthUserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate extends JpaService implements AuthUserDAO {

    public AuthUserDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.AUTH).createEntityManager());
    }

    @Override
    public void createUser(AuthUserEntity user) {
        user.setPassword(pe.encode(user.getPassword()));
        persist(user);
    }

    @Override
    public AuthUserEntity getUserFromAuthUserById(UUID userId) {
        return em.createQuery("select u from AuthUserEntity u where u.id=:userId", AuthUserEntity.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        return merge(user);
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        remove(user);
    }
}
