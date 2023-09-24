package guru.qa.niffler.tests;

import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileTests extends BaseWebTest {

    @BeforeEach
    @ApiLogin
    @DBUser
    void openProfilePage() {
        profilePage
                .openPage()
                .waitForPageIsLoaded();
    }

    @Test
    @DisplayName("WEB: Пользователь может изменить личные данные")
    void shouldChangeUserdataFromProfilePage() {
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
    void shouldUploadAvatar() {
        String filePath = "files/testimg.jpg";
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
    void shouldAddNewCategoryFromProfilePage() {
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
