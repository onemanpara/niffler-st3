package guru.qa.niffler.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.components.Header;
import guru.qa.niffler.components.Select;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage<ProfilePage> {

    private static final String PAGE_URL = config.nifflerFrontendUrl() + "profile";

    private final SelenideElement editAvatarButton = $("button.profile__avatar-edit");
    private final SelenideElement uploadAvatarInput = $(".edit-avatar__input[type=file]");
    private final SelenideElement avatar = $("img.profile__avatar");
    private final SelenideElement firstnameInput = $("[name=firstname]");
    private final SelenideElement surnameInput = $("[name=surname]");
    private final Select currencySelect = new Select($(byText("Currency")).$(".select-wrapper"));
    private final SelenideElement submitButton = $("button[type=submit]");

    private final SelenideElement categoryInput = $("[name=category]");
    private final SelenideElement addCategoryButton = $(".add-category__input-container button");
    private final ElementsCollection categories = $$(".categories__list .categories__item");


    @Override
    protected String getPageUrl() {
        return PAGE_URL;
    }

    @Step("Open profile page")
    public ProfilePage openPage() {
        Selenide.open(getPageUrl());
        return this;
    }

    @Override
    @Step("Wait for profile page is loaded")
    public ProfilePage waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        firstnameInput.shouldBe(visible);
        surnameInput.shouldBe(visible);
        currencySelect.getSelf().shouldBe(visible);
        return this;
    }

    @Step("Getting header")
    public Header getHeader() {
        return new Header();
    }

    @Step("Upload avatar")
    public ProfilePage uploadAvatarFromClasspath(String classpath) {
        editAvatarButton.click();
        uploadAvatarInput.uploadFromClasspath(classpath);
        return this;
    }

    @Step("Set firstname: {firstname}")
    public ProfilePage setFirstname(String firstname) {
        firstnameInput.setValue(firstname);
        return this;
    }

    @Step("Set surname: {surname}")
    public ProfilePage setSurname(String surname) {
        surnameInput.setValue(surname);
        return this;
    }

    @Step("Set currency: {currency}")
    public ProfilePage setCurrency(String currency) {
        currencySelect.selectOption(currency);
        return this;
    }

    @Step("Submit profile data")
    public ProfilePage submitData() {
        submitButton.scrollTo().click();
        return this;
    }

    @Step("Set category: {category}")
    public ProfilePage setCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }

    @Step("Create new category")
    public ProfilePage createCategory() {
        addCategoryButton.click();
        return this;
    }

    @Step("Check user has custom avatar")
    public ProfilePage checkUserHasCustomAvatar() {
        avatar.shouldNotHave(attribute("src", "/images/niffler_avatar.jpeg"));
        avatar.shouldHave(attribute("src"));
        return this;
    }

    @Step("Check firstname is: {firstname}")
    public ProfilePage checkFirstname(String firstname) {
        firstnameInput.shouldHave(value(firstname));
        return this;
    }

    @Step("Check surname is: {surname}")
    public ProfilePage checkSurname(String surname) {
        surnameInput.shouldHave(value(surname));
        return this;
    }

    @Step("Check currency is: {currency}")
    public ProfilePage checkCurrency(String currency) {
        currencySelect.checkSelectedOption(currency);
        return this;
    }

    @Step("Check categories list contains item: {category}")
    public ProfilePage checkCategoryIsExist(String category) {
        categories.find(text(category)).shouldBe(visible);
        return this;
    }

}
