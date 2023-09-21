package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.models.UserJson;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserQueueExtension.class);

    private static Map<User.UserType, Queue<UserJson>> usersQueue = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> usersWithFriends = new ConcurrentLinkedQueue<>();
        usersWithFriends.add(bindUser("misha", "12345"));
        usersWithFriends.add(bindUser("barsik", "12345"));
        usersQueue.put(User.UserType.WITH_FRIENDS, usersWithFriends);
        Queue<UserJson> usersInSent = new ConcurrentLinkedQueue<>();
        usersInSent.add(bindUser("bee", "12345"));
        usersInSent.add(bindUser("anna", "12345"));
        usersQueue.put(User.UserType.INVITATION_SENT, usersInSent);
        Queue<UserJson> usersInRc = new ConcurrentLinkedQueue<>();
        usersInRc.add(bindUser("valentin", "12345"));
        usersInRc.add(bindUser("pizzly", "12345"));
        usersQueue.put(User.UserType.INVITATION_RECEIVED, usersInRc);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        List<Method> methodsList = new ArrayList<>();
        methodsList.add(context.getRequiredTestMethod());
        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .forEach(methodsList::add);


        List<Parameter> parametersList = methodsList.stream()
                .map(Executable::getParameters)
                .flatMap(Arrays::stream)
                .filter(parameter -> parameter.getType().isAssignableFrom(UserJson.class))
                .filter(parameter -> parameter.isAnnotationPresent(User.class))
                .toList();

        Map<Pair<User.UserType, String>, UserJson> candidatesForTest = new ConcurrentHashMap<>();
        for (Parameter parameter : parametersList) {
            User parameterAnnotation = parameter.getAnnotation(User.class);
            User.UserType userType = parameterAnnotation.userType();
            Queue<UserJson> usersQueueByType = usersQueue.get(userType);

            UserJson candidateForTest = null;
            while (candidateForTest == null) {
                candidateForTest = usersQueueByType.poll();
            }
            candidateForTest.setUserType(userType);

            candidatesForTest.put(Pair.of(userType, parameter.getName()), candidateForTest);
            context.getStore(NAMESPACE).put(context.getUniqueId(), candidatesForTest);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<Pair<User.UserType, String>, UserJson> usersFromTest = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        for (Pair<User.UserType, String> userType : usersFromTest.keySet()) {
            usersQueue.get(userType.getLeft()).add(usersFromTest.get(userType));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class)
                && parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        User.UserType userType = parameterContext.getParameter().getAnnotation(User.class).userType();
        Pair<User.UserType, String> key = Pair.of(userType, parameterContext.getParameter().getName());
        return (UserJson) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class).get(key);
    }

    private static UserJson bindUser(String username, String password) {
        UserJson user = new UserJson();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

}
