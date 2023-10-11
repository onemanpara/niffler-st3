package guru.qa.niffler.tests.extension;

import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.GenerateUser;
import guru.qa.niffler.jupiter.annotations.GeneratedUser;
import guru.qa.niffler.models.UserJson;
import guru.qa.niffler.tests.web.BaseWebTest;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.GeneratedUser.Selector.NESTED;
import static guru.qa.niffler.jupiter.annotations.GeneratedUser.Selector.OUTER;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserSelectorTests extends BaseWebTest {

    @Test
    @ApiLogin(user = @GenerateUser)
    @GenerateUser
    void shouldResolveDifferentUsersFromAnnotation(@GeneratedUser(selector = NESTED) UserJson nestedCreatedUser,
                                                   @GeneratedUser(selector = OUTER) UserJson outerCreatedUser) {
        System.out.println(nestedCreatedUser.getUsername());
        System.out.println(outerCreatedUser.getUsername());
        assertNotEquals(nestedCreatedUser.getUsername(), outerCreatedUser.getUsername());
    }

}
