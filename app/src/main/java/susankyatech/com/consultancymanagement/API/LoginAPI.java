package susankyatech.com.consultancymanagement.API;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.Model.User;

public interface LoginAPI {
    @FormUrlEncoded
    @POST("login")
    Call<Login> userLogin(@Field("email")String username, @Field("password")String password);
}
