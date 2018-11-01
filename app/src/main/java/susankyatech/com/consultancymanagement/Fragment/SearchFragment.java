package susankyatech.com.consultancymanagement.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.susankya.wcbookstore.ItemDecorations.GridViewItemDecoration;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapter.ConsultancyListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MenuItem.OnMenuItemClickListener{

    @BindView(R.id.consultancy_list)
    RecyclerView recyclerView;
    @BindView(R.id.search_options)
    Spinner spinner;
    @BindView(R.id.searchCountry)
    SearchView searchCountry;
    @BindView(R.id.searchConsultancy)
    SearchView searchConsultancy;
    @BindView(R.id.searchCourse)
    SearchView searchCourse;
    @BindView(R.id.reset)
    ImageView reset;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;
    @BindView(R.id.whole_layout)
    RelativeLayout wholeLayout;
    @BindView(R.id.open_inquiry)
    RelativeLayout openInquiry;

    View view;
    private ClientAPI clientAPI;

    private Context context;

    private EditText qualification, interestedCountry, interestedCourse, summary;
    private TextView qualificationTv, completeYearTv, interestCourseTv, destination, testAttendedTv, summaryTv, messageTv;
    private Spinner completedYear;

    private int selectedYear;
    List<Integer> dates = new ArrayList<>();

    String[] options = { "Consultancy", "Course", "Country"};
    private String selected_options;

    ConsultancyListAdapter consultancyListAdapter;

    public static List<Client> clientList;
    private Data data;
    private EnquiryDetails enquiryDetails;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this,view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");
        init();
        return view;
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wholeLayout.setVisibility(View.GONE);
        openInquiry.setVisibility(View.GONE);

        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);

        ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item, options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_options = options[i];
                switch (selected_options){
                    case "Consultancy":
                        searchCourse.setVisibility(View.GONE);
                        searchConsultancy.setVisibility(View.VISIBLE);
                        searchCountry.setVisibility(View.GONE);
                        break;
                    case "Course":
                        searchCourse.setVisibility(View.VISIBLE);
                        searchConsultancy.setVisibility(View.GONE);
                        searchCountry.setVisibility(View.GONE);
                        break;
                    case "Country":
                        searchCourse.setVisibility(View.GONE);
                        searchConsultancy.setVisibility(View.GONE);
                        searchCountry.setVisibility(View.VISIBLE);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getAllConsultancy();

        openInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data = App.db().getObject(FragmentKeys.DATA,Data.class);
                if (data.enquiry_details == null) {
                    getStudentFurtherDetails();
                } else {
                    getEnquiry();
                }
            }
        });

        searchConsultancy.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String text = s;
                consultancyListAdapter.filter(text);
                return false;
            }
        });

        searchCourse.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                clientAPI.searchByCourse(s).enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null){
                                clientList = response.body().data.clients;
                                consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                                recyclerView.setAdapter(consultancyListAdapter);

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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchCountry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                progressLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                wholeLayout.setVisibility(View.GONE);
                clientAPI.searchByCountry(s).enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null){
                                progressLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                wholeLayout.setVisibility(View.VISIBLE);
                                openInquiry.setVisibility(View.VISIBLE);

                                clientList = response.body().data.clients;
                                consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());

                                recyclerView.setAdapter(consultancyListAdapter);

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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSearch();
            }
        });

    }

    private void getEnquiry() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("Tell us Your Requirement")
                .customView(R.layout.fragment_enquiry, true)
                .positiveText("Send Details")
                .negativeText("Edit Profile")
                .positiveColor(context.getResources().getColor(R.color.green))
                .negativeColor(context.getResources().getColor(R.color.blue))
                .show();


        qualificationTv = materialDialog.getCustomView().findViewById(R.id.qualification);
        completeYearTv = materialDialog.getCustomView().findViewById(R.id.complete_year);
        interestCourseTv = materialDialog.getCustomView().findViewById(R.id.destination);
        destination = materialDialog.getCustomView().findViewById(R.id.interested_course);
        testAttendedTv = materialDialog.getCustomView().findViewById(R.id.test_attended);
        summaryTv = materialDialog.getCustomView().findViewById(R.id.summary);
        messageTv = materialDialog.getCustomView().findViewById(R.id.message);

        messageTv.setVisibility(View.VISIBLE);

        getStudentInfo();

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editStudentDetails();
                materialDialog.dismiss();
            }
        });

    }

    private void editStudentDetails() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("Edit Your Profile")
                .customView(R.layout.fragment_course_enquiry, true)
                .positiveText("Save Details")
                .negativeText("Close")
                .positiveColor(context.getResources().getColor(R.color.green))
                .negativeColor(context.getResources().getColor(R.color.red))
                .show();

        qualification = materialDialog.getCustomView().findViewById(R.id.enquiry_level_completed);
        completedYear = materialDialog.getCustomView().findViewById(R.id.enquiry_complete_year);
        interestedCountry = materialDialog.getCustomView().findViewById(R.id.enquiry_apply_country);
        interestedCourse = materialDialog.getCustomView().findViewById(R.id.course_to_apply);
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);

        ArrayAdapter aa = new ArrayAdapter(context, android.R.layout.simple_spinner_item, dates);
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
                getEnquiry();
                materialDialog.dismiss();
            }
        });

    }

    private void getStudentInfo() {
        data = App.db().getObject(FragmentKeys.DATA, Data.class);
        enquiryDetails = data.enquiry_details;
        qualificationTv.setText(enquiryDetails.qualification);
        interestCourseTv.setText(enquiryDetails.interested_course);
        destination.setText(enquiryDetails.interested_country);
        summaryTv.setText(enquiryDetails.summary);
    }

    private void getStudentFurtherDetails() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                .title("Complete your Profile")
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


    private void addFurtherDetails(final MaterialDialog materialDialog) {
        String studentQualification = qualification.getText().toString();
        String studentInterestedCountry = interestedCountry.getText().toString();
        String studentInterestedCourse = interestedCourse.getText().toString();
        String studentSummary = summary.getText().toString();

        if (TextUtils.isEmpty(studentQualification)){
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentInterestedCountry)){
            interestedCountry.setError("Enter your Destination");
            interestedCountry.requestFocus();
        } else if (TextUtils.isEmpty(studentInterestedCourse)){
            interestedCourse.setError("Enter your Interested Course");
            interestedCourse.requestFocus();
        }  else if (TextUtils.isEmpty(studentSummary)){
            summary.setError("Enter Summary");
            summary.requestFocus();
        } else {
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetails(studentQualification, studentInterestedCountry, studentInterestedCourse, studentSummary, App.db().getInt(Keys.USER_ID))
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()){
                                if (response.body() != null){
                                    materialDialog.dismiss();
                                    App.db().putObject(FragmentKeys.DATA, response.body().data);
                                    MDToast mdToast = MDToast.makeText(getContext(), "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                }
                            }else {
                                try {
                                    Log.d("client", "onResponse: error" + response.errorBody().string());
                                    materialDialog.dismiss();
                                    MDToast mdToast = MDToast.makeText(getContext(), "There was something wrong while saving your info. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
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
    }

    private void resetSearch() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wholeLayout.setVisibility(View.GONE);
        clientAPI.getAllClients().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        wholeLayout.setVisibility(View.VISIBLE);
                        openInquiry.setVisibility(View.VISIBLE);

                        clientList = response.body().data.clients;
                        consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        recyclerView.setAdapter(consultancyListAdapter);

                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }

    private void getAllConsultancy() {
        clientAPI.getAllClients().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        wholeLayout.setVisibility(View.VISIBLE);
                        openInquiry.setVisibility(View.VISIBLE);

                        clientList = response.body().data.clients;
                        consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        recyclerView.setAdapter(consultancyListAdapter);
//                        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(16));
                        recyclerView.addItemDecoration(new GridViewItemDecoration(context));

                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
