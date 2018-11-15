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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentRegisterFragment extends Fragment {

    @BindView(R.id.fullName)
    EditText fullName;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.rePassword)
    EditText rePassword;
    @BindView(R.id.txtLogin)
    TextView txtLogin;
    @BindView(R.id.btnSignUp)
    FancyButton signUp;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.gender)
    Spinner gender;

    String[] sex = { "Male", "Female" };
    private String userGender;

    private ProgressDialog progressDialog;

    public StudentRegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_student_register, container, false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }

    private void init() {
        progressDialog = new ProgressDialog(getContext());
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, sex);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender.setAdapter(aa);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userGender = sex[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = fullName.getText().toString();
                String userEmail = email.getText().toString();
                String userPhone = phone.getText().toString();
                String userPassword = password.getText().toString();
                String userRePassword = rePassword.getText().toString();
                String userAddress = address.getText().toString();


                if (TextUtils.isEmpty(userName)){
                    fullName.setError("Enter your name");
                    fullName.requestFocus();
                }else if (TextUtils.isEmpty(userEmail)){
                    email.setError("Enter your email");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(userPhone)){
                    phone.setError("Enter your phone");
                    phone.requestFocus();
                }else if (TextUtils.isEmpty(userAddress)){
                    address.setError("Enter your address");
                    address.requestFocus();
                }else if (TextUtils.isEmpty(userPassword)){
                    password.setError("Enter your password");
                    password.requestFocus();
                }else if (TextUtils.isEmpty(userRePassword)){
                    rePassword.setError("Enter your confirm password");
                    rePassword.requestFocus();
                }else if (!userPassword.equals(userRePassword)){
                    rePassword.setError("Enter password doesn't match");
                    rePassword.requestFocus();
                } else {
                    progressDialog.setTitle("Signing up");
                    progressDialog.setMessage("Please wait, while we are creating your account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    registerStudent(userName, userEmail, userPhone, userPassword, userAddress, userGender);
                }
            }
        });
        
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new StudentLoginFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void registerStudent(String userName, String userEmail, String userPhone, String userPassword, String userAddress, String userGender) {
        LoginAPI loginAPI = App.consultancyRetrofit().create(LoginAPI.class);
        loginAPI.studentRegister(userName, userEmail, userPhone, userPassword, userAddress , userGender).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() != null) {
                    App.db().putBoolean(Keys.USER_LOGIN_ATTEMPT, true);
                    Log.d("asdasd", "onResponse: "+response);
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
                            MDToast mdToast = MDToast.makeText(getActivity(), "There was something wrong with your registration. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                            mdToast.show();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    try {
                        progressDialog.dismiss();
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getActivity(), "Applicant Already Exists. Please try again!"+response.errorBody().string(), Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
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

    private void goToMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

}
