package susankyatech.com.consultancymanagement.API;

import retrofit2.Call;
import retrofit2.http.GET;
import susankyatech.com.consultancymanagement.Model.Login;

public interface BannerAPI {
    @GET("banners/")
    Call<Login> getBanners();

    @GET("nav-banner/")
    Call<Login> getNavBanner();
}
