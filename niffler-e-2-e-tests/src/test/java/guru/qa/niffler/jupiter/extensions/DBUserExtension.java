package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.repository.UserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.*;

public class DBUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserRepositoryHibernate userRepository = new UserRepositoryHibernate();

        List<Method> methodsList = new ArrayList<>();
        methodsList.add(context.getRequiredTestMethod());
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .forEach(methodsList::add);

        Map<String, AuthUserEntity> usersForTest = new HashMap<>();
        for (Method method : methodsList) {
            DBUser annotation = method.getAnnotation(DBUser.class);
            if (annotation != null) {
                AuthUserEntity user = createAuthUserEntity(annotation);
                userRepository.createUserForTest(user);
                usersForTest.put(method.getName(), user);
            }
        }
        context.getStore(NAMESPACE).put(context.getUniqueId(), usersForTest);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        UserRepositoryHibernate userRepositoryHibernate = new UserRepositoryHibernate();
        Map<String, AuthUserEntity> usersFromTest = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        usersFromTest.values().forEach(userRepositoryHibernate::removeUser);
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

    private AuthUserEntity createAuthUserEntity(DBUser annotation) {
        Faker faker = new Faker();
        AuthUserEntity user = new AuthUserEntity();
        final String password = annotation.password().isEmpty() ? faker.internet().password(3, 12) : annotation.password();
        user.setUsername(annotation.username().isEmpty() ? faker.name().username() : annotation.username());
        user.setPassword(password);
        user.setEncodedPassword(password);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(new ArrayList<>(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(user);
                    return ae;
                }).toList()));
        return user;
    }

}
