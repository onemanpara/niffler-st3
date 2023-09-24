package guru.qa.niffler.tests;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.WebTest;
import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import guru.qa.niffler.jupiter.extensions.DBUserExtension;
import guru.qa.niffler.pages.*;
import org.junit.jupiter.api.extension.ExtendWith;

@WebTest
@ExtendWith({DBUserExtension.class, ApiLoginExtension.class})
public abstract class BaseWebTest {

    protected static final Config CFG = Config.getInstance();

    protected final WelcomePage welcomePage = new WelcomePage();
    protected final LoginPage loginPage = new LoginPage();
    protected final MainPage mainPage = new MainPage();
    protected final FriendsPage friendsPage = new FriendsPage();
    protected final PeoplePage peoplePage = new PeoplePage();
    protected final ProfilePage profilePage = new ProfilePage();

}
