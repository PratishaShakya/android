package susankyatech.com.consultancymanagement.API;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import susankyatech.com.consultancymanagement.Model.GalleryDeleteResponse;
import susankyatech.com.consultancymanagement.Model.ProfileInfo;
import susankyatech.com.consultancymanagement.Model.Detail;
import susankyatech.com.consultancymanagement.Model.Login;

public interface ClientAPI {
    @GET("client/")
    Call<Login> getClient();

    @POST("client/detail")
    Call<Login> addClient(@Body ProfileInfo detail);

    @POST("client/detail")
    Call<Login> addCountry(@Body ProfileInfo detail);

    @POST("client/detail")
    Call<Login> addCourse(@Body ProfileInfo detail);

    @GET("students/clients/")
    Call<Login> getAllClients();

    @Multipart
    @POST("client/cover-photo/")
    Call<Login> addCoverPicture(@Part MultipartBody.Part cover_photo);

    @GET("students/client/{id}")
    Call<Login> getSingleClient(@Path("id") int id);

    @GET("students/matching-clients")
    Call<Login> getMatchingClient();

    @GET("students/interested-clients")
    Call<Login> getInterestedClient(@Query("status") String type);

    @GET("students/clients")
    Call<Login> searchByCourse(@Query("courses") String course);

    @GET("students/clients")
    Call<Login> searchByCountry(@Query("countries") String country);

    @Multipart
    @POST("client/add-logo")
    Call<ResponseBody> addLogo(@Part MultipartBody.Part logo);

    @FormUrlEncoded
    @POST("students/change-primary-info")
    Call<Login> changePrimaryInfo(@Field("email") String email,
                                   @Field("name") String name,
                                   @Field("address") String address,
                                   @Field("phone") String phone);

    @DELETE("client/gallery/{id}")
    Call<GalleryDeleteResponse> deleteGalleryImage(@Path("id") int id);

}
