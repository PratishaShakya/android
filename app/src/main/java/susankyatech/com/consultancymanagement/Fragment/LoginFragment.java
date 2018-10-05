package susankyatech.com.consultancymanagement.Fragment;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.valdesekamdem.library.mdtoast.MDToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.LoginAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Activity.WelcomeActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.btn_login)
    FancyButton btnLogin;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                String uEmail = email.getText().toString().trim();
                String pw = password.getText().toString();

                if (uEmail.isEmpty() || pw.isEmpty()) {
                    MDToast mdToast = MDToast.makeText(getActivity(), "Please enter both username and password!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                    mdToast.show();
                } else login(uEmail, pw);
            }
        });

    }

    private void login(String uEmail, String pw) {
        Log.d(TAG, "login: "+uEmail + " " + pw);
        Log.d(TAG, "login: "+ App.BASE_URL);
        LoginAPI loginAPI = App.consultancyRetrofit().create(LoginAPI.class);
        retrofit2.Call<Login> call = loginAPI.userLogin(uEmail, pw);
        call.enqueue(new Callback<Login>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() != null) {
                    App.db().putBoolean(Keys.USER_LOGIN_ATTEMPT, true);
                    Log.d("asdasd", "onResponse: "+response);
                    if (response.isSuccessful() && !response.body().data.user.jwt_token.isEmpty()) {

                        App.db().putObject(Keys.USER_LOGIN, response.body());
                        App.db().putInt(Keys.USER_ID, response.body().data.user.id);
                        App.db().putString(Keys.USER_TOKEN, response.body().data.user.jwt_token);
                        App.db().putBoolean(Keys.USER_LOGGED_IN, true);
                        App.db().putObject(FragmentKeys.CLIENT, response.body().data.user.client);
                        Log.d(TAG, "onResponse: check"+response.body().data.user.client.detail);
                        if(response.body().data.user.client.detail==null){
                            goToWelcomeActivity();
                        } else {
//                            goToMainActivity();
                            goToWelcomeActivity();
                        }

                    } else {
                        try {
                            MDToast mdToast = MDToast.makeText(getActivity(), "There was something wrong with your login. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                            mdToast.show();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getActivity(), "Email address and password doesn't match. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There is no internet connection. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void goToWelcomeActivity() {
        startActivity(new Intent(getActivity(), WelcomeActivity.class));
        getActivity().finish();
    }

    private void goToMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
