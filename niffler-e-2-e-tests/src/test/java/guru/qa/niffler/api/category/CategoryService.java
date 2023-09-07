package guru.qa.niffler.api.category;

import guru.qa.niffler.models.CategoryJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CategoryService {

    @POST("/category")
    Call<CategoryJson> addCategory(@Body CategoryJson categoryJson);

}