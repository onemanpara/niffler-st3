package guru.qa.niffler.components;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class Notification extends BaseComponent<Notification> {

    public Notification() {
        super($(".Toastify__toast"));
    }

    @Step("Check that notification text is: {notificationText}")
    public Notification checkText(String notificationText) {
        self.shouldBe(visible).shouldHave(text(notificationText));
        return this;
    }

}
