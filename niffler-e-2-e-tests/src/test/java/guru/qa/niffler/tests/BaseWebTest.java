package guru.qa.niffler.tests;

import guru.qa.niffler.jupiter.annotations.WebTest;
import guru.qa.niffler.pages.*;

@WebTest
public abstract class BaseWebTest {

    protected final WelcomePage welcomePage = new WelcomePage();
    protected final LoginPage loginPage = new LoginPage();
    protected final MainPage mainPage = new MainPage();
    protected final FriendsPage friendsPage = new FriendsPage();
    protected final PeoplePage peoplePage = new PeoplePage();
    protected final ProfilePage profilePage = new ProfilePage();

}
