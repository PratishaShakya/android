package susankyatech.com.consultancymanagement.API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import susankyatech.com.consultancymanagement.Model.ProfileInfo;
import susankyatech.com.consultancymanagement.Model.Detail;
import susankyatech.com.consultancymanagement.Model.Login;

public interface ClientAPI {
    @GET("client/")
    Call<Login> getClient();

   @POST("client/detail")
    Call<Login> addClient (@Body ProfileInfo detail);

    @GET("students/clients/")
    Call<Login> getAllClients();

    @POST("client/cover-photo/")
    Call<Login> addCoverPicture(@Body Detail detail);

    @GET("students/client/{id}")
    Call<Login> getSingleClient(@Path("id") int id);

    @GET("students/matching-clients")
    Call<Login> getMatchingClient();

 @GET("students/interested-clients")
 Call<Login> getInterestedClient();

    @GET("students/clients")
    Call<Login> searchByCourse(@Query("courses") String course);

    @GET("students/clients")
    Call<Login> searchByCountry(@Query("countries") String country);

}
