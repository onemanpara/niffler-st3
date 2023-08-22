package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    private static final String PAGE_URL = "http://127.0.0.1:9000/login";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button.form__submit");

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Override
    @Step("Wait for login page is loaded")
    public LoginPage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        return this;
    }

    @Step("Set username: {username}")
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: {password}")
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Success submit login form")
    public MainPage successSubmit() {
        submitButton.click();
        return new MainPage();
    }

}
