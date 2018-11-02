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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
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
    @BindView(R.id.address)
    TextView addressTv;
    @BindView(R.id.contact)
    TextView contactTv;
    @BindView(R.id.name)
    TextView nameTv;
    @BindView(R.id.email)
    TextView emailIdTv;
    @BindView(R.id.test_attended)
    TextView testAttendedTv;
    @BindView(R.id.summary)
    TextView summaryTv;
    @BindView(R.id.btn_edit)
    FancyButton editInfo;


    private EditText qualification, summary, userName, userEmail, userAddress, userPhone;
    private Spinner completedYear, qualificationSpinner;
    private CheckBox ieltsCB,toeflCB,greCB,pteCB,satCB;

    List<Integer> dates = new ArrayList<>();
    String[] qualificationList = {"+2", "Bachelors", "Masters"};
    private String testAttended, selectedLevel;

    List<String> testsAttendedList=new ArrayList<>();
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
        qualificationTv.setText(enquiryDetails.qualification.get(0) + ", " + enquiryDetails.qualification.get(1));
        nameTv.setText(data.name);
        addressTv.setText(data.address);
        contactTv.setText(data.phone);
        emailIdTv.setText(data.email);
        summaryTv.setText(enquiryDetails.summary);
        completeYear.setText(enquiryDetails.completed_year);
        testAttendedTv.setText(enquiryDetails.test_attended);
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
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);
        satCB=materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB=materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB=materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB=materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB=materialDialog.getCustomView().findViewById(R.id.cv_tofel);

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
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);
        qualificationSpinner = materialDialog.getCustomView().findViewById(R.id.qualification_spinner);
        userName = materialDialog.getCustomView().findViewById(R.id.enquiry_name);
        userAddress = materialDialog.getCustomView().findViewById(R.id.enquiry_address);
        userEmail = materialDialog.getCustomView().findViewById(R.id.enquiry_email);
        userPhone = materialDialog.getCustomView().findViewById(R.id.enquiry_phone);
        satCB=materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB=materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB=materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB=materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB=materialDialog.getCustomView().findViewById(R.id.cv_tofel);

        ArrayAdapter dateAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,dates);
        ArrayAdapter levelAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,qualificationList);

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        completedYear.setAdapter(dateAdapter);
        qualificationSpinner.setAdapter(levelAdapter);

        completedYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = dates.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        qualificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLevel = qualificationList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        String datafromApi = enquiryDetails.qualification.get(0);
        for(int i=0; i < qualificationList.length; i++){
            if (qualificationList[i].equals(datafromApi)){
                qualificationSpinner.setSelection(i);
            }
        }

        enquiryDetails = data.enquiry_details;
        Log.d(TAG, "editStudentDetails: "+enquiryDetails.qualification.get(0));
        qualification.setText(enquiryDetails.qualification.get(1));
        summary.setText(enquiryDetails.summary);
        completeYear.setText(enquiryDetails.completed_year);
        testAttendedTv.setText(enquiryDetails.test_attended);
        userEmail.setText(data.email);
        userName.setText(data.name);
        userPhone.setText(data.phone);
        userAddress.setText(data.address);

        String[] testsSplit=enquiryDetails.test_attended.split(",");

        for (int i=0;i<testsSplit.length;i++)
        {
            testsSplit[i]=testsSplit[i].trim();
        }

        Collections.addAll(testsAttendedList,testsSplit);

        CheckBox[] checkBoxes=new CheckBox[]{ieltsCB,toeflCB,satCB,pteCB,greCB};

        for (int i=0;i<checkBoxes.length;i++)
        {
            if (testsAttendedList.contains(checkBoxes[i].getText().toString()))
                checkBoxes[i].setChecked(true);
        }
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

    private String getTestsString()
    {
        List<String> chosenTests=new ArrayList<>();
        String tests="";
        if (toeflCB.isChecked())
            chosenTests.add("TOEFL");
        if (satCB.isChecked())
            chosenTests.add("SAT");
        if (greCB.isChecked())
            chosenTests.add("GRE");
        if (ieltsCB.isChecked())
            chosenTests.add("IELTS");
        if (pteCB.isChecked())
            chosenTests.add("PTE");

        for (int i=0;i<chosenTests.size();i++)
        {
            String test=chosenTests.get(i);
            if (i==chosenTests.size()-1)
            tests+=test;
            else tests+=test+", ";
        }

        return tests;
    }

    private void addFurtherDetails(final MaterialDialog materialDialog) {
        String studentQualification = qualification.getText().toString();
        String studentSummary = summary.getText().toString();
        final String studentName = userName.getText().toString();
        final String studentEmail = userEmail.getText().toString();
        final String studentAddress = userAddress.getText().toString();
        final String studentPhone = userPhone.getText().toString();
        String testsAttended = getTestsString();

        if (TextUtils.isEmpty(studentQualification)){
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)){
            summary.setError("Enter your qualification");
            summary.requestFocus();
        } else {
            String userQualification = selectedLevel + "," + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(userQualification, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear, testsAttended)
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()){
                                if (response.body() != null){
                                    editStudentPrimaryInfo(studentName, studentEmail, studentAddress, studentPhone, materialDialog);
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

    private void editStudentPrimaryInfo(String studentName, String studentEmail, String studentAddress, String studentPhone, final MaterialDialog materialDialog) {

        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.changePrimaryInfo(studentEmail, studentName, studentAddress, studentPhone)
                .enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null){
                                App.db().putObject(FragmentKeys.DATA, response.body().data);
                                materialDialog.dismiss();
                                MDToast mdToast = MDToast.makeText(getContext(), "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                mdToast.show();
                                Log.d(TAG, "onResponse: "+response.body().data.name);
                                getStudentInfo();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {

                    }
                });
    }

}
