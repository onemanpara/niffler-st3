package guru.qa.niffler.api.register;

import guru.qa.niffler.api.RestService;
import guru.qa.niffler.api.context.CookieContext;
import guru.qa.niffler.api.interceptor.RecievedCodeInterceptor;
import io.qameta.allure.Step;

import java.io.IOException;

public class RegisterServiceClient extends RestService {

    private final RegisterService registerService = retrofit.create(RegisterService.class);

    public RegisterServiceClient() {
        super(CFG.nifflerAuthUrl() + "/register/", false, new RecievedCodeInterceptor());
    }

    @Step("Register user")
    public void registerUser(String username, String password) throws IOException {
        CookieContext cookieContext = CookieContext.getInstance();
        registerService.register().execute();
        registerService.registerUser(
                cookieContext.getXsrfTokenFormattedCookie(),
                cookieContext.getXsrfTokenCookieValue(),
                username,
                password,
                password
        ).execute();
    }

}
