package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class RegistrationPage extends BasePage<RegistrationPage> {

    private static final String PAGE_URL = "http://127.0.0.1:9000/register";

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement confirmPasswordInput = $("#passwordSubmit");
    private final SelenideElement singUpButton = $("button.form__submit");
    private final SelenideElement successRegisterMessage = $$(".form__paragraph").first();
    private final SelenideElement loginButton = $("a[href*='/redirect']");

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Override
    @Step("Wait for registration page is loaded")
    public RegistrationPage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        confirmPasswordInput.shouldBe(visible);
        singUpButton.shouldBe(visible);
        return this;
    }

    @Step("Set username: {username}")
    public RegistrationPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: {password}")
    public RegistrationPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Set confirm password: {confirmPassword}")
    public RegistrationPage setConfirmPassword(String confirmPassword) {
        confirmPasswordInput.setValue(confirmPassword);
        return this;
    }

    @Step("Submit registration form")
    public RegistrationPage submit() {
        singUpButton.click();
        return this;
    }

    @Step("Check success register message is: {message}")
    public RegistrationPage checkSuccessRegisterMessageIsVisible(String message) {
        successRegisterMessage.shouldBe(visible).shouldHave(text(message));
        return this;
    }

    @Step("Go to login page")
    public LoginPage login() {
        loginButton.click();
        return new LoginPage();
    }

}
