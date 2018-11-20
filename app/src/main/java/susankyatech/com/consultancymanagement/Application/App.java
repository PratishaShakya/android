package susankyatech.com.consultancymanagement.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import susankyatech.com.consultancymanagement.Activity.LoginActivity;
import susankyatech.com.consultancymanagement.Generic.Client;
import susankyatech.com.consultancymanagement.Generic.Keys;

public class App extends Application {
    public static String BASE_URL = Client.HTTPS + Client.BASE_URL_API;
    public static TinyDB tinyDB;

    @Override
    public void onCreate() {
        super.onCreate();
        tinyDB = new TinyDB(this);
        Iconify
                .with(new FontAwesomeModule())
                .with(new MaterialCommunityModule())
                .with(new MaterialModule())
                .with(new SimpleLineIconsModule())
                .with(new IoniconsModule());
    }

    public static TinyDB db() {
        return tinyDB;
    }

    public static Retrofit consultancyRetrofit(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        if (App.db().getBoolean(Keys.USER_LOGGED_IN)) {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES);
            okHttpBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + App.db().getString(Keys.USER_TOKEN))
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
            builder.client(okHttpBuilder.build());
        }
        Retrofit retrofit = builder.build();
        return retrofit;
    }

    public static Retrofit noHeaderConsultancyRetrofit(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit;
    }

    public static void logOut(Activity activity) {
        App.db().clear(); //Removes all the content of SharedPreferences
        Intent i = new Intent(activity, LoginActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

}
