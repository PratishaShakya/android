package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentProfileFragment extends Fragment {

    @BindView(R.id.qualification)
    TextView qualificationTv;
    @BindView(R.id.complete_year)
    TextView completeYear;
    @BindView(R.id.interested_course)
    TextView interestCourseTv;
    @BindView(R.id.destination)
    TextView destination;
    @BindView(R.id.test_attended)
    TextView testAttendedTv;
    @BindView(R.id.summary)
    TextView summaryTv;
    @BindView(R.id.btn_edit)
    FancyButton editInfo;


    private EditText qualification, interestedCountry, interestedCourse, summary;
    private Spinner completedYear;
    private CheckBox ielts, tofel, pte, gre;

    List<Integer> dates = new ArrayList<>();
    private String testAttended;
    private int selectedYear;

    private EnquiryDetails enquiryDetails;
    private Data data;

    public StudentProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        init();
        return view;
    }

    private void init() {

        data = App.db().getObject(FragmentKeys.DATA,Data.class);
        if (data.enquiry_details == null){
            getStudentFurtherDetails();
        }else{
            getStudentInfo();
        }

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = todayYear; i > 1969; i--){
            dates.add(i);
        }

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editStudentDetails();
            }
        });
    }

    private void getStudentInfo() {
        data = App.db().getObject(FragmentKeys.DATA,Data.class);
        enquiryDetails = data.enquiry_details;
        qualificationTv.setText(enquiryDetails.qualification);
        interestCourseTv.setText(enquiryDetails.interested_course);
        destination.setText(enquiryDetails.interested_country);
        summaryTv.setText(enquiryDetails.summary);
    }

    private void getStudentFurtherDetails() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                .title("Complete Your Profile")
                .customView(R.layout.fragment_course_enquiry, true)
                .positiveText("Save")
                .negativeText("Close")
                .positiveColor(getResources().getColor(R.color.green))
                .negativeColor(getResources().getColor(R.color.red))
                .show();

        qualification = materialDialog.getCustomView().findViewById(R.id.enquiry_level_completed);
        completedYear = materialDialog.getCustomView().findViewById(R.id.enquiry_complete_year);
        interestedCountry = materialDialog.getCustomView().findViewById(R.id.enquiry_apply_country);
        interestedCourse = materialDialog.getCustomView().findViewById(R.id.course_to_apply);
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFurtherDetails(materialDialog);
            }
        });
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
    }

    private void editStudentDetails() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                .title("Edit Your Profile")
                .customView(R.layout.fragment_course_enquiry, true)
                .positiveText("Save Details")
                .negativeText("Close")
                .positiveColor(getResources().getColor(R.color.green))
                .negativeColor(getResources().getColor(R.color.red))
                .show();

        qualification = materialDialog.getCustomView().findViewById(R.id.enquiry_level_completed);
        completedYear = materialDialog.getCustomView().findViewById(R.id.enquiry_complete_year);
        interestedCountry = materialDialog.getCustomView().findViewById(R.id.enquiry_apply_country);
        interestedCourse = materialDialog.getCustomView().findViewById(R.id.course_to_apply);
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);

        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,dates);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        completedYear.setAdapter(aa);
        completedYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = dates.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        enquiryDetails = data.enquiry_details;
        qualification.setText(enquiryDetails.qualification);
        interestedCourse.setText(enquiryDetails.interested_course);
        interestedCountry.setText(enquiryDetails.interested_country);
        summary.setText(enquiryDetails.summary);

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFurtherDetails(materialDialog);
//
            }
        });
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });

    }

    private void addFurtherDetails(final MaterialDialog materialDialog) {
        String studentQualification = qualification.getText().toString();
        String studentInterestedCountry = interestedCountry.getText().toString();
        String studentInterestedCourse = interestedCourse.getText().toString();
        String studentSummary = summary.getText().toString();

        if (TextUtils.isEmpty(studentQualification)){
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentInterestedCountry)){
            interestedCountry.setError("Enter your qualification");
            interestedCountry.requestFocus();
        } else if (TextUtils.isEmpty(studentInterestedCourse)){
            interestedCourse.setError("Enter your qualification");
            interestedCourse.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)){
            summary.setError("Enter your qualification");
            summary.requestFocus();
        } else {
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetails(studentQualification, studentInterestedCountry, studentInterestedCourse, studentSummary, App.db().getInt(Keys.USER_ID))
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()){
                                if (response.body() != null){
                                    App.db().putObject(FragmentKeys.DATA, response.body().data);
                                    MDToast mdToast = MDToast.makeText(getContext(), "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                                    mdToast.show();
                                    getStudentInfo();
                                    materialDialog.dismiss();
                                }
                            }else {
                                try {
                                    Log.d("client", "onResponse: error" + response.errorBody().string());
                                    MDToast mdToast = MDToast.makeText(getContext(), "There was something wrong while saving your info. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
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
    }

}
