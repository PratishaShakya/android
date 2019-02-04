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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import susankyatech.com.consultancymanagement.API.ClientInterestAPI;
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
public class OpenInquiryProfileFragment extends Fragment {

    @BindView(R.id.qualification)
    TextView qualificationTv;
    @BindView(R.id.complete_year)
    TextView completeYearTv;
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
    @BindView(R.id.dob)
    TextView dobTv;
    @BindView(R.id.btn_edit)
    FancyButton btnEdit;
    @BindView(R.id.btn_back)
    FancyButton btnBack;
    @BindView(R.id.btn_send)
    FancyButton btnSend;

    private int clientId;
    private String clientName;

    private Data data;
    private EnquiryDetails enquiryDetails;

    private int selectedYear;
    private EditText qualification, summary, userName, userEmail, userAddress, userPhone, userDOB, year, month, day;
    private Spinner completedYear, qualificationSpinner;
    private CheckBox ieltsCB,toeflCB,greCB,pteCB,satCB;

    List<Integer> dates = new ArrayList<>();
    String[] qualificationList = {"+2", "Bachelors", "Masters"};
    List<String> testsAttendedList=new ArrayList<>();

    private String testAttended, selectedLevel;


    public OpenInquiryProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_open_inquiry_profile, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        if (getArguments() != null){
            clientId = getArguments().getInt("client_id");
            clientName = getArguments().getString("client_name");
        }

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = todayYear; i > 1969; i--) {
            dates.add(i);
        }

        getStudentInfo();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clientId == 0){
                    Bundle bundle = new Bundle();
                    bundle.putInt("client_id",clientId);
                    bundle.putString("client_name", clientName);

                    FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    OpenInquirySelectCountryFragment openInquirySelectCountryFragment = new OpenInquirySelectCountryFragment();
                    openInquirySelectCountryFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.main_container, openInquirySelectCountryFragment).addToBackStack(null).commit();
                }else {
                    FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    OpenInquirySelectCountryFragment openInquirySelectCountryFragment = new OpenInquirySelectCountryFragment();
                    fragmentTransaction.replace(R.id.main_container, openInquirySelectCountryFragment).addToBackStack(null).commit();
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editStudentDetails();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clientId == 0){
                    sendInquiry();
                } else {
                    sendInquiryToClient();
                }

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
        userDOB = materialDialog.getCustomView().findViewById(R.id.enquiry_dob);
        year = materialDialog.getCustomView().findViewById(R.id.year);
        month = materialDialog.getCustomView().findViewById(R.id.month);
        day = materialDialog.getCustomView().findViewById(R.id.day);
        satCB=materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB=materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB=materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB=materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB=materialDialog.getCustomView().findViewById(R.id.cv_tofel);

        ArrayAdapter dateAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, dates);
        ArrayAdapter levelAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,qualificationList);

        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        qualificationSpinner.setAdapter(levelAdapter);
        completedYear.setAdapter(dateAdapter);


        qualificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLevel = qualificationList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        completedYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = dates.get(i);
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
        testAttendedTv.setText(enquiryDetails.test_attended);
        userEmail.setText(data.email);
        userName.setText(data.name);
        userPhone.setText(data.phone);
        userAddress.setText(data.address);

        String[] newDob = data.dob.split("-");
        year.setText(newDob[0]);
        month.setText(newDob[1]);
        day.setText(newDob[2]);

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

    private String getTestsString() {
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
        String studentQualification = qualification.getText().toString().trim();
        String studentSummary = summary.getText().toString().trim();
        final String studentName = userName.getText().toString().trim();
        final String studentEmail = userEmail.getText().toString().trim();
        final String studentAddress = userAddress.getText().toString().trim();
        final String studentPhone = userPhone.getText().toString().trim();
        String testsAttended = getTestsString().trim();
        String yrs = year.getText().toString().trim();
        String mth = month.getText().toString().trim();
        String days = day.getText().toString().trim();

        if (TextUtils.isEmpty(studentQualification)){
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)){
            summary.setError("Enter your qualification");
            summary.requestFocus();
        } else {
            String studentDOB = yrs + "-" + mth + "-" + days;
            String userQualification = selectedLevel + ", " + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(userQualification, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear, testsAttended)
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()){
                                if (response.body() != null){
                                    editStudentPrimaryInfo(studentName, studentEmail, studentAddress, studentPhone, materialDialog, studentDOB);
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

    private void editStudentPrimaryInfo(String studentName, String studentEmail, String studentAddress, String studentPhone, final MaterialDialog materialDialog, String studentDOB) {

        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.changePrimaryInfo(studentEmail, studentName, studentAddress, studentPhone, studentDOB)
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

    private void sendInquiryToClient() {
        ClientInterestAPI clientInterestAPI = App.consultancyRetrofit().create(ClientInterestAPI.class);
        clientInterestAPI.interestedOnClient(clientId, 0, 1, 0).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        SearchFragment searchFragment = new SearchFragment();
                        fragmentTransaction.replace(R.id.main_container, searchFragment).addToBackStack(null).commit();

                        MDToast mdToast = MDToast.makeText(getContext(), "Inquiry is successfully sent to " + clientName, Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                        mdToast.show();
                    }
                }else {
                    try {
                        Log.d("client", "onResponse: error" + response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
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

    private void sendInquiry() {
        ClientInterestAPI clientInterestAPI = App.consultancyRetrofit().create(ClientInterestAPI.class);
        clientInterestAPI.interestedOnClient(0, 0,0,1).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        SearchFragment searchFragment = new SearchFragment();
                        fragmentTransaction.replace(R.id.main_container, searchFragment).addToBackStack(null).commit();

                        MDToast mdToast = MDToast.makeText(getContext(), "Inquiry is sent successfully", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                        mdToast.show();
                    }
                }else {
                    try {
                        Log.d("client", "onResponse: error" + response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
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

    private void getStudentInfo() {
        data = App.db().getObject(FragmentKeys.DATA,Data.class);
        enquiryDetails = data.enquiry_details;
        qualificationTv.setText(enquiryDetails.qualification.get(0) + "," + enquiryDetails.qualification.get(1));
        nameTv.setText(data.name);
        addressTv.setText(data.address);
        contactTv.setText(data.phone);
        emailIdTv.setText(data.email);
        summaryTv.setText(enquiryDetails.summary);
        if (data.dob != null) {
            dobTv.setText(data.dob);
        }
        completeYearTv.setText(enquiryDetails.completed_year);
        testAttendedTv.setText(enquiryDetails.test_attended);
    }

}
