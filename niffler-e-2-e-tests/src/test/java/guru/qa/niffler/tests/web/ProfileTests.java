package guru.qa.niffler.tests.web;

import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileTests extends BaseWebTest {

    @Test
    @DisplayName("WEB: Пользователь может изменить личные данные")
    @ApiLogin(user = @GenerateUser)
    void shouldChangeUserdataFromProfilePage() {
        profilePage
                .openPage()
                .waitForPageIsLoaded();

        profilePage
                .setFirstname("Иван")
                .setSurname("Иванов")
                .setCurrency("USD")
                .submitData()
                .getNotification()
                .checkText("Profile updated!");

        profilePage
                .openPage()
                .waitForPageIsLoaded()
                .checkFirstname("Иван")
                .checkSurname("Иванов")
                .checkCurrency("USD");
    }

    @Test
    @DisplayName("WEB: Пользователь может загрузить аватар")
    @ApiLogin(user = @GenerateUser)
    void shouldUploadAvatar() {
        String filePath = "files/testimg.jpg";
        profilePage
                .openPage()
                .waitForPageIsLoaded();
        profilePage
                .uploadAvatarFromClasspath(filePath)
                .submitData()
                .getNotification()
                .checkText("Profile updated!");

        profilePage
                .openPage()
                .waitForPageIsLoaded()
                .checkUserHasCustomAvatar();
    }

    @Test
    @DisplayName("WEB: Пользователь может добавить новую категорию трат")
    @ApiLogin(user = @GenerateUser)
    void shouldAddNewCategoryFromProfilePage() {
        profilePage
                .openPage()
                .waitForPageIsLoaded();
        profilePage
                .setCategory("Покупки в магазине")
                .createCategory()
                .getNotification()
                .checkText("New category added!");

        profilePage
                .openPage()
                .waitForPageIsLoaded()
                .checkCategoryIsExist("Покупки в магазине");
    }

}
