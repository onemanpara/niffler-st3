package guru.qa.niffler.pages;

import guru.qa.niffler.components.Header;
import io.qameta.allure.Step;

public class MainPage extends BasePage<MainPage> {

    private static final String PAGE_URL = "http://127.0.0.1:3000/main";

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Override
    @Step("Wait for main page is loaded")
    public MainPage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        return this;
    }

    @Step("Getting header")
    public Header getHeader() {
        return new Header();
    }

}
