package susankyatech.com.consultancymanagement.API;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import susankyatech.com.consultancymanagement.Model.Gallery;
import susankyatech.com.consultancymanagement.Model.GalleryAdmin;
import susankyatech.com.consultancymanagement.Model.Login;

public interface GalleryAPI {

    @GET("client/gallery/")
    Call<Login> getGalleries();

   @Multipart
    @POST("client/gallery/")
    Call<ResponseBody> addGalleries(@Part MultipartBody.Part[] images);
}
