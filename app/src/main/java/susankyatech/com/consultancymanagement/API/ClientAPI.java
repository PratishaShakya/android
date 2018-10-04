package susankyatech.com.consultancymanagement.API;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.Detail;
import susankyatech.com.consultancymanagement.Model.Login;

public interface ClientAPI {
    @GET("client/")
    Call<Login> getClient();

   @Multipart
    @POST("client/detail")
    Call<Login> addClientDetail(@Part MultipartBody.Part cover_photo,
                                @PartMap Map<String, RequestBody> detail);

}
