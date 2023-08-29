package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.db.dao.*;
import guru.qa.niffler.jupiter.annotations.Dao;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class DaoExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if ((field.getType().isAssignableFrom(AuthUserDAO.class) || field.getType().isAssignableFrom(UserDataUserDAO.class))
                    && field.isAnnotationPresent(Dao.class)) {
                field.setAccessible(true);

                AuthUserDAO authUserDAO;

                if ("hibernate".equals(System.getProperty("db.impl"))) {
                    authUserDAO = new AuthUserDAOHibernate();
                } else if ("spring".equals(System.getProperty("db.impl"))) {
                    authUserDAO = new AuthUserDAOSpringJdbc();
                } else {
                    authUserDAO = new AuthUserDAOJdbc();
                }

                field.set(testInstance, authUserDAO);
            }

        }

    }
}
