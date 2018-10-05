package susankyatech.com.consultancymanagement.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import susankyatech.com.consultancymanagement.Model.Gallery;
import susankyatech.com.consultancymanagement.Model.Login;

public interface GalleryAPI {

    @GET("client/gallery/")
    Call<Login> getGalleries();

    @POST("client/gallery/")
    Call<ResponseBody> addGalleries(@Body Gallery gallery);
}
