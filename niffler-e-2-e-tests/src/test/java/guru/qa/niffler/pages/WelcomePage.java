package guru.qa.niffler.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage extends BasePage<WelcomePage> {

    private static final String PAGE_URL = "http://127.0.0.1:3000/";

    private final SelenideElement loginButton = $("a[href*='/redirect']");

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
    public WelcomePage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        return this;
    }

    @Step("Go to login page")
    public LoginPage login() {
        loginButton.click();
        return new LoginPage();
    }

}
