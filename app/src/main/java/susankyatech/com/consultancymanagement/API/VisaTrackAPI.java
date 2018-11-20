package susankyatech.com.consultancymanagement.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.Model.Result;

public interface VisaTrackAPI {

    @GET("/mobile/status-code/")
    Call<Result> showVisaProcess(@Query("code") String code,
                                 @Query("dob") String dob);
}
