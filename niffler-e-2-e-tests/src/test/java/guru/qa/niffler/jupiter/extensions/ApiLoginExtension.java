package guru.qa.niffler.jupiter.extensions;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.auth.AuthServiceClient;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.context.SessionStorageContext;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;

import java.io.IOException;
import java.util.Map;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    private final AuthServiceClient authServiceClient = new AuthServiceClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        ApiLogin loginAnnotation = extensionContext.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        if (loginAnnotation != null) {
            String username, password;

            DBUser userAnnotation = extensionContext.getRequiredTestMethod().getAnnotation(DBUser.class);

            if (userAnnotation != null) {
                Map<String, AuthUserEntity> users = extensionContext
                        .getStore(DBUserExtension.NAMESPACE)
                        .get(extensionContext.getUniqueId(), Map.class);
                AuthUserEntity user = users.get(extensionContext.getRequiredTestMethod().getName());
                username = user.getUsername();
                password = user.getEncodedPassword();
            } else if (!loginAnnotation.username().isEmpty() && !loginAnnotation.password().isEmpty()) {
                username = loginAnnotation.username();
                password = loginAnnotation.password();
            } else throw new IllegalArgumentException("Can't find the login data.");

            doLogin(username, password);
        }
    }


    private void doLogin(String username, String password) {
        SessionStorageContext sessionStorageContext = SessionStorageContext.getInstance();
        sessionStorageContext.init();

        try {
            authServiceClient.doLogin(username, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Selenide.open(Config.getInstance().nifflerFrontendUrl());
        Selenide.sessionStorage().setItem("codeChallenge", sessionStorageContext.getCodeChallenge());
        Selenide.sessionStorage().setItem("id_token", sessionStorageContext.getToken());
        Selenide.sessionStorage().setItem("codeVerifier", sessionStorageContext.getCodeVerifier());
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", CookieContext.getInstance().getJSessionIdCookieValue());
        WebDriverRunner.getWebDriver().manage().addCookie(jsessionIdCookie);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        SessionStorageContext.getInstance().clearContext();
        CookieContext.getInstance().clearContext();
    }
}
