package guru.qa.niffler.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.PeoplePage;
import guru.qa.niffler.pages.ProfilePage;
import guru.qa.niffler.pages.WelcomePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($(".header"));
    }

    private final SelenideElement friendsButton = self.$("a[href*='/friends']");
    private final SelenideElement peopleButton = self.$("a[href*='/people']");
    private final SelenideElement profileButton = self.$("a[href*='/profile']");
    private final SelenideElement logoutButton = self.$("[data-tooltip-id=logout] button");


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

    @Step("Go to profile page")
    public ProfilePage goToProfilePage() {
        profileButton.click();
        return new ProfilePage();
    }

    @Step("Logout")
    public WelcomePage logout() {
        logoutButton.click();
        return new WelcomePage();
    }

}
