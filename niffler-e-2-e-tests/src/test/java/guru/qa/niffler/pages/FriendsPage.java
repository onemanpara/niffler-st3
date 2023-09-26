package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.components.PeopleTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage<FriendsPage> {

    private static final String PAGE_URL = CFG.nifflerFrontendUrl() + "/friends";

    private final SelenideElement tableContainer = $(".people-content");
    private final PeopleTable table = new PeopleTable($(".table"));

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Override
    @Step("Wait for friends page is loaded")
    public FriendsPage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        tableContainer.shouldBe(visible);
        return this;
    }

    @Step("Check that friends list contains friend")
    public FriendsPage checkUserHaveFriend() {
        table.getAllRows().filter(text("You are friends")).shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check that friends list contains friend with name: {name}")
    public FriendsPage checkUserHaveFriend(String name) {
        table.getAllRows().filter(text(name + " You are friends")).shouldHave(size(1));
        return this;
    }

    @Step("Check that friends list contains friend invitation")
    public FriendsPage checkUserHaveFriendInvitation() {
        table.getRowsWithFriendInvitation().shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Check that friends list contains friend invitation from user: {username}")
    public FriendsPage checkUserHaveFriendInvitation(String username) {
        table.getRowsWithFriendInvitation().filter(text(username)).shouldHave(size(1));
        return this;
    }

}
