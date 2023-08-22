package guru.qa.niffler.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;

public abstract class BasePage<T extends BasePage> {

    protected abstract String getPageUrl();

    public T waitForPageIsLoaded() {
        Selenide.webdriver().shouldHave(WebDriverConditions.urlContaining(getPageUrl()));
        return (T) this;
    }

}
