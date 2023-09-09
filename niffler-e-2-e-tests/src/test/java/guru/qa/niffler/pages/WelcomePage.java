package guru.qa.niffler.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class WelcomePage extends BasePage<WelcomePage> {

    private static final String PAGE_URL = "http://127.0.0.1:3000/";

    private final SelenideElement loginButton = $("a[href*='/redirect']");
    private final SelenideElement registerButton = $("a[href*='/register']");

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Step("Open welcome page")
    public WelcomePage openPage() {
        Selenide.open(getPageUrl());
        return this;
    }

    @Override
    @Step("Wait for welcome page is loaded")
    public WelcomePage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        loginButton.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }

    @Step("Go to login page")
    public LoginPage login() {
        loginButton.click();
        return new LoginPage();
    }

    @Step("Go to register page")
    public RegistrationPage register() {
        registerButton.click();
        return new RegistrationPage();
    }

}
