package guru.qa.niffler.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;

public class PeopleTable extends BaseComponent<PeopleTable> {

    public PeopleTable(SelenideElement self) {
        super(self);
    }

    public ElementsCollection getAllRows() {
        return $$("tbody tr");
    }

    public ElementsCollection getRowsWithFriendInvitation() {
        return $$("table tr:has([data-tooltip-id='submit-invitation'])");
    }

}
