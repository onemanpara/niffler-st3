package guru.qa.niffler.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.components.PeopleTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage extends BasePage<PeoplePage> {

    private static final String PAGE_URL = CFG.nifflerFrontendUrl() + "/people";

    private final SelenideElement tableContainer = $(".people-content");
    private final PeopleTable table = new PeopleTable($(".table"));

    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Step("Open people page")
    public PeoplePage openPage() {
        Selenide.open(getPageUrl());
        return this;
    }

    @Override
    @Step("Wait for people page is loaded")
    public PeoplePage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        tableContainer.shouldBe(visible);
        return this;
    }

    @Step("Send friend invitation")
    public PeoplePage sendFriendInvitation() {
        final SelenideElement userRow = table.getAllRows().first();
        final SelenideElement actionCell = table.getActionsCell(userRow);
        actionCell.$("button.button-icon_type_add").click();
        return this;
    }

    @Step("Check that people list contains sent friend invitation")
    public PeoplePage checkInvitationToFriendSent() {
        table.getAllRows().filter(text("Pending invitation")).shouldHave(sizeGreaterThan(0));
        return this;
    }

}
