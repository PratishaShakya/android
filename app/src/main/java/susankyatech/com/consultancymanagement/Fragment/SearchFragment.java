package susankyatech.com.consultancymanagement.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.susankya.wcbookstore.ItemDecorations.GridViewItemDecoration;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.ClientInterestAPI;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapter.ConsultancyListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Generic.Utilities;
import susankyatech.com.consultancymanagement.Model.BannerItem;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.support.constraint.Constraints.TAG;

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
    @BindView(R.id.sliderLayout)
    SliderLayout sliderLayout;

    View view;
    private ClientAPI clientAPI;

    private Context context;

    private EditText qualification, summary;
    private Spinner completedYear, qualificationSpinner, countryList;
    private CheckBox ieltsCB,toeflCB,greCB,pteCB,satCB;

    private int selectedYear;
    private String selected_options, selectedLevel;

    List<Integer> dates = new ArrayList<>();
    List<BannerItem> bannerItems;
    String[] qualificationList = {"+2", "Bachelors", "Masters"};
    String[] options = { "Consultancy", "Course", "Country"};

    ArrayAdapter dateAdapter, optionsAdapter, levelAdapter;

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


        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = todayYear; i > 1969; i--){
            dates.add(i);
        }

        optionsAdapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item, options);
        dateAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, dates);
        levelAdapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item, qualificationList);

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

        if (Utilities.isConnectionAvailable(getActivity())) {
            emptyView.setVisibility(View.GONE);
            getSearchItems();
        }else{
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
                if (s.equals("")){
                    resetSearch();
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
                if (s.equals("")){
                    resetSearch();
                }
                return false;
            }
        });

    }

    private void getSearchItems() {
        getAllConsultancy();
        getSLiderItems();
    }

    private void getSLiderItems() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerInside();

        bannerItems = new ArrayList<>();
        bannerItems.add(new BannerItem("https://www.revive-adserver.com/media/GitHub.jpg"));
        bannerItems.add(new BannerItem("https://tctechcrunch2011.files.wordpress.com/2017/02/android-studio-logo.png"));
        bannerItems.add(new BannerItem("data:image/jpeg;base64,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"));
        bannerItems.add(new BannerItem("https://www.fifthsun.com/media/catalog/category/FPTLP_Liverpool_Banner_760x200_1.jpg"));

        for (int i = 0; i < bannerItems.size(); i++) {
            Log.d(TAG, "getSLiderItems: "+bannerItems.get(i).banner);
            DefaultSliderView sliderView = new DefaultSliderView(getActivity());
            sliderView
                    .image(bannerItems.get(i).banner)
                    .setRequestOption(requestOptions)
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true);

            sliderLayout.addSlider(sliderView);
        }
        // set Slider Transition Animation
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);


    }

    private void doRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        resetSearch();

    }

    private void getEnquiry() {
        FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
        OpenInquirySelectCountryFragment openInquirySelectCountryFragment = new OpenInquirySelectCountryFragment();
        fragmentTransaction.replace(R.id.main_container, openInquirySelectCountryFragment).addToBackStack(null).commit();

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
        ieltsCB = materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        toeflCB = materialDialog.getCustomView().findViewById(R.id.cv_tofel);
        satCB = materialDialog.getCustomView().findViewById(R.id.cv_sat);
        greCB = materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB = materialDialog.getCustomView().findViewById(R.id.cv_pte);

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


    private void addFurtherDetails(final MaterialDialog materialDialog) {
        String studentQualification = qualification.getText().toString();
        String studentSummary = summary.getText().toString();
        String testsAttended=getTestsString();

        if (TextUtils.isEmpty(studentQualification)){
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)){
            summary.setError("Enter Summary");
            summary.requestFocus();
        } else {
            String studentCourseCompleted = selectedLevel + ", " + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(studentCourseCompleted, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear, testsAttended)
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

    private String getTestsString()
    {
        String tests="";
        if (toeflCB.isChecked())
            tests+="TOEFL, ";
        if (satCB.isChecked())
            tests+="SAT, ";
        if (greCB.isChecked())
            tests+="GRE, ";
        if (ieltsCB.isChecked())
            tests+="IELTS, ";
        if (pteCB.isChecked())
            tests+="PTE";

        return tests;
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
