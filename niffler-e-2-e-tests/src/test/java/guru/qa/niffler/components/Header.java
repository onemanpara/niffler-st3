package guru.qa.niffler.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.PeoplePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($(".header"));
    }

    private final SelenideElement friendsButton = self.$("a[href*='/friends']");
    private final SelenideElement peopleButton = self.$("a[href*='/people']");

    @Step("Go to friends page")
    public FriendsPage goToFriendsPage() {
        friendsButton.click();
        return new FriendsPage();
    }

    @Step("Go to people page")
    public PeoplePage goToPeoplePage() {
        peopleButton.click();
        return new PeoplePage();
    }

}
