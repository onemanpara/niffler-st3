package guru.qa.niffler.api.friend;

import guru.qa.niffler.api.RestService;
import guru.qa.niffler.models.UserJson;
import io.qameta.allure.Step;

import java.io.IOException;

public class FriendServiceClient extends RestService {

    private final FriendService friendService = retrofit.create(FriendService.class);

    public FriendServiceClient() {
        super(CFG.nifflerUserdataUrl());
    }

    @Step("Add friend")
    public void addFriend(String token, String jsessionid, UserJson userJson) throws IOException {
        friendService.addFriend("Bearer " + token, jsessionid, userJson).execute();
    }

    @Step("Accept invitation")
    public void acceptInvitation(String token, String jsessionid, UserJson userJson) throws IOException {
        friendService.acceptInvitation("Bearer " + token, jsessionid, userJson).execute();
    }

}
