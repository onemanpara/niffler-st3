package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.*;

public class DBUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {


    private static final AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    private static final UserDataUserDAO userDataUserDAO = new AuthUserDAOJdbc();

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        List<Method> methodsList = new ArrayList<>();
        methodsList.add(context.getRequiredTestMethod());
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .forEach(methodsList::add);
        Map<String, UserEntity> userForTest = new HashMap<>();
        for (Method method : methodsList) {
            DBUser annotation = method.getAnnotation(DBUser.class);
            UserEntity user = new UserEntity();
            user.setUsername(annotation.username());
            user.setPassword(annotation.password());
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
            userDataUserDAO.createUserInUserData(user);
            userForTest.put(method.getName(), user);
            context.getStore(NAMESPACE).put(context.getUniqueId(), userForTest);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<String, UserEntity> usersFromTest = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        usersFromTest.values().stream()
                .map(UserEntity::getId)
                .forEach(authUserDAO::deleteUserByIdInAuth);
        usersFromTest.values().stream()
                .map(UserEntity::getUsername)
                .forEach(userDataUserDAO::deleteUserByUsernameInUserData);
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Map<String, UserEntity> users = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
        return users.get(parameterContext.getDeclaringExecutable().getName());
    }
}
