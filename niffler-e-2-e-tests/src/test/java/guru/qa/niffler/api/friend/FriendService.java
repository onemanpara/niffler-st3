package guru.qa.niffler.api.friend;

import guru.qa.niffler.models.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FriendService {

    @POST("/addFriend")
    Call<Void> addFriend(
            @Header("Authorization") String token,
            @Header("Cookie") String jsessionid,
            @Body UserJson userJson);

    @POST("/acceptInvitation")
    Call<Void> acceptInvitation(
            @Header("Authorization") String token,
            @Header("Cookie") String jsessionid,
            @Body UserJson userJson);
}
