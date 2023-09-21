package guru.qa.niffler.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class Select extends BaseComponent<Select> {

    private final SelenideElement dropdownButton = self.$x(".//*[contains(@class, 'indicatorContainer')]");
    private final ElementsCollection options = self.$$x(".//*[contains(@id, 'option')]");
    private final SelenideElement selectedValue = self.$x(".//*[contains(@class, 'singleValue')]");

    public Select(SelenideElement self) {
        super(self);
    }

    public Select selectOption(String option) {
        dropdownButton.scrollTo().click();
        options.find(text(option)).click();
        return this;
    }

    public Select checkSelectedOption(String expectedValue) {
        selectedValue.shouldBe(visible).shouldHave(text(expectedValue));
        return this;
    }

}
