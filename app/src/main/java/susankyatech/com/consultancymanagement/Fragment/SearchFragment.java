package susankyatech.com.consultancymanagement.Fragment;


import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.BannerAPI;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapters.ConsultancyListAdapter;
import susankyatech.com.consultancymanagement.Adapters.HomeAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Decorations.GridViewItemDecoration;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Generic.Utilities;
import susankyatech.com.consultancymanagement.Interfaces.HomeItems;
import susankyatech.com.consultancymanagement.Model.Banner;
import susankyatech.com.consultancymanagement.Model.BannerGrid;
import susankyatech.com.consultancymanagement.Model.BannerItem;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.ConsultancyGrid;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    @BindView(R.id.home_item_list)
    RecyclerView recyclerView;
    @BindView(R.id.search_options)
    Spinner spinner;
    @BindView(R.id.searchCountry)
    SearchView searchCountry;
    @BindView(R.id.searchConsultancy)
    SearchView searchConsultancy;
    @BindView(R.id.searchCourse)
    SearchView searchCourse;
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
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.emtpyTextview)
    TextView emptyText;
    @BindView(R.id.emptyTextLayout)
    View emptyView;
    @BindView(R.id.empty_img)
    ImageView empty;


    View view;
    private ClientAPI clientAPI;

    private Context context = getContext();

    private EditText qualification, summary, userName, userEmail, userAddress, userPhone, year, month, day;
    private Spinner completedYear, qualificationSpinner;
    private CheckBox ieltsCB, toeflCB, greCB, pteCB, satCB;

    private int selectedYear;
    private String selected_options, selectedLevel;

    List<Integer> dates = new ArrayList<>();
    List<BannerItem> bannerItems=new ArrayList<>();
    String[] qualificationList = {"+2", "Bachelors", "Masters"};
    String[] options = {"Consultancy", "Course", "Country"};

    ArrayAdapter dateAdapter, optionsAdapter, levelAdapter;

    ConsultancyListAdapter consultancyListAdapter;

    public static List<Client> clientList;
    private Data data;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Consultancy Finder");
        init();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.notification_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                NotificationFragment notificationFragment = new NotificationFragment();
                fragmentTransaction.replace(R.id.main_container, notificationFragment).addToBackStack(null).commit();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = todayYear; i > 1969; i--) {
            dates.add(i);
        }

        optionsAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, options);
        dateAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, dates);
        levelAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, qualificationList);

        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(optionsAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_options = options[i];
                switch (selected_options) {
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

        if (Utilities.isConnectionAvailable(getActivity())) {
            emptyView.setVisibility(View.GONE);
            getAllConsultancy();
        } else {
            progressLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            wholeLayout.setVisibility(View.GONE);
            openInquiry.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            empty.setImageDrawable(getResources().getDrawable(R.drawable.ic_plug));
            emptyText.setText("OOPS, out of Connection");
        }

        openInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data = App.db().getObject(FragmentKeys.DATA, Data.class);
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
                if (text.trim().length() == 0){
                    getAllConsultancy();
                }else {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    recyclerView.setAdapter(consultancyListAdapter);
                }

                return false;
            }
        });

        searchCourse.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                clientAPI.searchByCourse(s).enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                clientList = response.body().data.clients;
                                consultancyListAdapter = new ConsultancyListAdapter(clientList, getContext());
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                recyclerView.setAdapter(consultancyListAdapter);
                                if (recyclerView.getItemDecorationCount() == 0) {
                                    recyclerView.addItemDecoration(new GridViewItemDecoration(getContext()));
                                }
                            }
                        } else {
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
                if (s.equals("")) {
                    getAllConsultancy();
                }
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
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                progressLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                wholeLayout.setVisibility(View.VISIBLE);
                                openInquiry.setVisibility(View.VISIBLE);

                                clientList = response.body().data.clients;
                                consultancyListAdapter = new ConsultancyListAdapter(clientList, getContext());
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                recyclerView.setAdapter(consultancyListAdapter);

                            }
                        } else {
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
                if (s.equals("")) {
                    getAllConsultancy();
                }
                return false;
            }
        });

    }
    private void getSLiderItems(List<Client> clientList) {
        BannerAPI bannerAPI = App.consultancyRetrofit().create(BannerAPI.class);
        bannerAPI.getBanners().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body()!=null){
                        bannerItems = new ArrayList<>();
                        List<Banner> bannerList = response.body().data.banners;
                        for (int i = 0; i < bannerList.size(); i ++){
                            if (bannerList.get(i).nav_ad == 0){
                                bannerItems.add(new BannerItem(bannerList.get(i).ad_image));
                            }
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(new HomeAdapter(context, displayHomeItems(clientList)));
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
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
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);
        qualificationSpinner = materialDialog.getCustomView().findViewById(R.id.qualification_spinner);
        userName = materialDialog.getCustomView().findViewById(R.id.enquiry_name);
        userAddress = materialDialog.getCustomView().findViewById(R.id.enquiry_address);
        userEmail = materialDialog.getCustomView().findViewById(R.id.enquiry_email);
        userPhone = materialDialog.getCustomView().findViewById(R.id.enquiry_phone);
        year = materialDialog.getCustomView().findViewById(R.id.year);
        month = materialDialog.getCustomView().findViewById(R.id.month);
        day = materialDialog.getCustomView().findViewById(R.id.day);
        satCB = materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB = materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB = materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB = materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB = materialDialog.getCustomView().findViewById(R.id.cv_tofel);

        userEmail.setText(data.email);
        userName.setText(data.name);
        userPhone.setText(data.phone);
        userAddress.setText(data.address);

        String[] newDob = data.dob.split("-");
        year.setText(newDob[0]);
        month.setText(newDob[1]);
        day.setText(newDob[2]);

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

    private void doRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getAllConsultancy();
        recyclerView.removeItemDecoration(new GridViewItemDecoration(getContext()));
    }

    private void getEnquiry() {
        FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
        OpenInquirySelectCountryFragment openInquirySelectCountryFragment = new OpenInquirySelectCountryFragment();
        fragmentTransaction.replace(R.id.main_container, openInquirySelectCountryFragment).addToBackStack(null).commit();
    }

    private void addFurtherDetails(final MaterialDialog materialDialog) {
        String studentQualification = qualification.getText().toString();
        String studentSummary = summary.getText().toString();
        String testsAttended = getTestsString();

        if (TextUtils.isEmpty(studentQualification)) {
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)) {
            summary.setError("Enter Summary");
            summary.requestFocus();
        } else {
            String studentCourseCompleted = selectedLevel + ", " + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(studentCourseCompleted, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear, testsAttended)
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    materialDialog.dismiss();
                                    App.db().putObject(FragmentKeys.DATA, response.body().data);
                                    MDToast mdToast = MDToast.makeText(getContext(), "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                }
                            } else {
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

    private String getTestsString() {
        String tests = "";
        if (toeflCB.isChecked())
            tests += "TOEFL, ";
        if (satCB.isChecked())
            tests += "SAT, ";
        if (greCB.isChecked())
            tests += "GRE, ";
        if (ieltsCB.isChecked())
            tests += "IELTS, ";
        if (pteCB.isChecked())
            tests += "PTE";

        return tests;
    }

    private void getAllConsultancy() {
        Log.d(TAG, "getAllConsultancy:  hi");
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wholeLayout.setVisibility(View.GONE);
        openInquiry.setVisibility(View.GONE);
        clientAPI.getAllClients().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        wholeLayout.setVisibility(View.VISIBLE);
                        openInquiry.setVisibility(View.VISIBLE);

                        clientList = response.body().data.clients;
                        consultancyListAdapter = new ConsultancyListAdapter(clientList, getContext());
                        getSLiderItems(clientList);
                    }

                }else {
                    try {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getContext(), "Error on getting client lists. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Log.d(ContentValues.TAG, "onFailure: "+t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There is connection problem while retrieving CLients. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                mdToast.show();
            }
        });
    }

    private List<HomeItems> displayHomeItems(List<Client> clientList) {
        List<HomeItems> homeItemsList = new ArrayList<>();
        homeItemsList.add(new BannerGrid(bannerItems));
        homeItemsList.add(new ConsultancyGrid(clientList));
        return homeItemsList;
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
