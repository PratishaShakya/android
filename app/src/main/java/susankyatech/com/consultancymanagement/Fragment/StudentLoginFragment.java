package susankyatech.com.consultancymanagement.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentLoginFragment extends Fragment {

    @BindView(R.id.btn_login)
    FancyButton btnLogin;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.txtSignUp)
    TextView txtSignUp;
    @BindView(R.id.txtConsultancy)
    TextView txtConsultancyLogin;

    private ProgressDialog progressDialog;

    public StudentLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_login, container, false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }

    private void init() {
        progressDialog = new ProgressDialog(getContext());
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                
                if (TextUtils.isEmpty(userEmail)){
                    email.setError("Please enter your email!");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(userPassword)){
                    password.setError("Please enter your password!");
                    password.requestFocus();
                }else{
                    progressDialog.setTitle("Logging in");
                    progressDialog.setMessage("Please wait, while we are logging in your account.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    loginStudent(userEmail, userPassword);
                }
                
            }
        });
        
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new StudentRegisterFragment()).addToBackStack(null).commit();
            }
        });

        txtConsultancyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new AdminLoginFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void loginStudent(String userEmail, String userPassword) {
        LoginAPI loginAPI = App.consultancyRetrofit().create(LoginAPI.class);
        loginAPI.studentLogin(userEmail, userPassword).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() != null) {
                    App.db().putBoolean(Keys.USER_LOGIN_ATTEMPT, true);
                    if (response.isSuccessful() && !response.body().data.jwt_token.isEmpty()) {
                        App.db().putObject(Keys.USER_LOGIN, response.body());
                        App.db().putInt(Keys.USER_ID, response.body().data.id);
                        App.db().putString(Keys.USER_TOKEN, response.body().data.jwt_token);
                        App.db().putBoolean(Keys.USER_LOGGED_IN, true);
                        App.db().putBoolean(Keys.IS_STUDENT, response.body().data.is_student);
                        App.db().putObject(FragmentKeys.DATA, response.body().data);
                        progressDialog.dismiss();
                        goToMainActivity();

                    } else {
                        try {
                            progressDialog.dismiss();
                            MDToast mdToast = MDToast.makeText(getActivity(), "There was something wrong with login. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                            mdToast.show();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    try {
                        progressDialog.dismiss();
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getActivity(), "Email address and password doesn't match. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

}
