package susankyatech.com.consultancymanagement.Fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.VisaTrackAPI;
import susankyatech.com.consultancymanagement.Activity.LoginActivity;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Model.Result;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisaTrackingFragment extends Fragment {

    @BindView(R.id.student_code)
    EditText studentCode;
    @BindView(R.id.year)
    EditText year;
    @BindView(R.id.month)
    EditText month;
    @BindView(R.id.day)
    EditText day;
    @BindView(R.id.btn_view_status)
    FancyButton viewStatus;

    private String fragmentName;

    public VisaTrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visa_tracking, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        if (getArguments()!=null){
            fragmentName = getArguments().getString(FragmentKeys.FRAGMENTNAME);
        }
        if (fragmentName!=null){

        } else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Visa Tracking");
        }
        viewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = studentCode.getText().toString();
                String yrs = year.getText().toString();
                String mth = month.getText().toString();
                String days = day.getText().toString();

                if (TextUtils.isEmpty(code)){
                    studentCode.setError("Enter Student Code");
                    studentCode.requestFocus();
                } else if (TextUtils.isEmpty(yrs)){
                    year.setError("Enter year");
                    year.requestFocus();
                } else if (TextUtils.isEmpty(mth)){
                    month.setError("Enter month");
                    month.requestFocus();
                } else if (TextUtils.isEmpty(yrs)){
                    day.setError("Enter day");
                    day.requestFocus();
                } else {
                    String dob = yrs + "-" + mth + "-" + days;
                    trackVisa(code, dob);
                }
            }
        });
    }

    private void trackVisa(String code, String dob) {
        VisaTrackAPI visaTrackAPI = App.noHeaderConsultancyRetrofit().create(VisaTrackAPI.class);
        visaTrackAPI.showVisaProcess(code, dob).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        App.db().putObject(FragmentKeys.RESULT, response.body());

                        if (fragmentName!=null){
                            FragmentTransaction fragmentTransaction = ((LoginActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            VisaTrackingViewStatusFragment visaTrackingViewStatusFragment = new VisaTrackingViewStatusFragment();
                            fragmentTransaction.replace(R.id.login_container, visaTrackingViewStatusFragment).addToBackStack(null).commit();
                        }else {
                            FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            VisaTrackingViewStatusFragment visaTrackingViewStatusFragment = new VisaTrackingViewStatusFragment();
                            fragmentTransaction.replace(R.id.main_container, visaTrackingViewStatusFragment).addToBackStack(null).commit();
                        }
                    }
                }else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getContext(), "Error on retrieving visa processs status. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.toString());
            }
        });


    }

}
