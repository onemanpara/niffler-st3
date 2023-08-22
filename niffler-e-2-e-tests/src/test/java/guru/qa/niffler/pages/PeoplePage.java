package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.components.PeopleTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage extends BasePage<PeoplePage> {

    private static final String PAGE_URL = "http://127.0.0.1:3000/people";

    private final SelenideElement tableContainer = $(".people-content");
    private final PeopleTable table = new PeopleTable($(".table"));

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Override
    @Step("Wait for people page is loaded")
    public PeoplePage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        tableContainer.shouldBe(visible);
        return this;
    }

    @Step("Check that people list contains sent friend invitation")
    public PeoplePage checkInvitationToFriendSent() {
        table.getAllRows().filter(text("Pending invitation")).shouldHave(size(2));
        return this;
    }

}
