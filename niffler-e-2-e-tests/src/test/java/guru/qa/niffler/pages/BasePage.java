package guru.qa.niffler.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;
import guru.qa.niffler.components.Notification;
import guru.qa.niffler.config.Config;

public abstract class BasePage<T extends BasePage> {

    protected static final Config CFG = Config.getInstance();

    protected abstract String getPageUrl();

    public T waitForPageIsLoaded() {
        Selenide.webdriver().shouldHave(WebDriverConditions.urlContaining(getPageUrl()));
        return (T) this;
    }

    public Notification getNotification() {
        return new Notification();
    }

}
