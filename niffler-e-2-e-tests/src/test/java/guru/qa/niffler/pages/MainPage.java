package guru.qa.niffler.pages;

import guru.qa.niffler.components.Header;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

    private static final String PAGE_URL = CFG.nifflerFrontendUrl() + "/main";

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Override
    @Step("Wait for main page is loaded")
    public MainPage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        $("section.main-content__section-stats").shouldBe(visible);
        return this;
    }

    @Step("Getting header")
    public Header getHeader() {
        return new Header();
    }

}
