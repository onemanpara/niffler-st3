package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataDAO;
import guru.qa.niffler.db.dao.impl.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.impl.UserDataDAOJdbc;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.*;

public class DBUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {


    private static final AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    private static final UserDataDAO userDataDAO = new UserDataDAOJdbc();

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        List<Method> methodsList = new ArrayList<>();
        methodsList.add(context.getRequiredTestMethod());
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .forEach(methodsList::add);
        Map<String, AuthUserEntity> userForTest = new HashMap<>();
        for (Method method : methodsList) {
            DBUser annotation = method.getAnnotation(DBUser.class);
            if (annotation != null) {
                AuthUserEntity user = new AuthUserEntity();
                Faker faker = new Faker();
                user.setUsername(annotation.username().isEmpty() ? faker.name().username() : annotation.username());
                user.setPassword(annotation.password().isEmpty() ? faker.internet().password() : annotation.password());
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

                authUserDAO.createUser(user);
                userDataDAO.createUserInUserData(user);
                userForTest.put(method.getName(), user);
                context.getStore(NAMESPACE).put(context.getUniqueId(), userForTest);
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<String, AuthUserEntity> usersFromTest = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        usersFromTest.values().stream()
                .map(AuthUserEntity::getId)
                .forEach(authUserDAO::deleteUserByIdInAuth);
        usersFromTest.values().stream()
                .map(AuthUserEntity::getUsername)
                .forEach(userDataDAO::deleteUserByUsernameInUserData);
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(AuthUserEntity.class);
    }

    @Override
    public AuthUserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Map<String, AuthUserEntity> users = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
        return users.get(parameterContext.getDeclaringExecutable().getName());
    }
}
