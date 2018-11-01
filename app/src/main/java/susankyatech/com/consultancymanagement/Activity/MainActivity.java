package susankyatech.com.consultancymanagement.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Fragment.AddGalleryFragment;
import susankyatech.com.consultancymanagement.Fragment.ConsultancyProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.GalleryFragment;
import susankyatech.com.consultancymanagement.Fragment.InterestedClientsFragment;
import susankyatech.com.consultancymanagement.Fragment.MatchingClientsFragment;
import susankyatech.com.consultancymanagement.Fragment.SearchFragment;
import susankyatech.com.consultancymanagement.Fragment.StudentProfileFragment;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawable_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_app_bar)
    Toolbar mToolbar;

    private EditText qualification, interestedCountry, interestedCourse, summary;

    private Spinner completedYear, qualificationSpinner;
    private String fragmentName, selectedLevel;
    private int selectedYear;

    private CheckBox ieltsCB,toeflCB,greCB,pteCB,satCB;

    List<Integer> dates = new ArrayList<>();
    List<String> qualificationList = new ArrayList<>();

    private Data data;
    private Client client;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Home");
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        qualificationList.add("+2");
        qualificationList.add("Bachelors");
        qualificationList.add("Masters");

        for (int i = todayYear; i > 1969; i--){
            dates.add(i);
        }

        View navView = navigationView.inflateHeaderView(R.layout.nav_header_layout);
        TextView userName = navView.findViewById(R.id.user_name);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelector(item);
                return false;
            }
        });


        if (App.db().getBoolean(Keys.IS_STUDENT)) {
            navigationView.inflateMenu(R.menu.navigation_menu_student);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchFragment()).commit();
            data = App.db().getObject(FragmentKeys.DATA,Data.class);
            Log.d("poi", "init: "+data);
            userName.setText(data.name);
            if (data.enquiry_details == null){

                getStudentFurtherDetails();
            }
        }
        else {
            navigationView.inflateMenu(R.menu.navigation_menu_admin);

            client = App.db().getObject(FragmentKeys.CLIENT,Client.class);

            userName.setText(client.client_name);
            if (getIntent() != null){
                fragmentName = getIntent().getStringExtra(FragmentKeys.FRAGMENTNAME);
                if (fragmentName == null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ConsultancyProfileFragment()).commit();
                } else if (fragmentName.equals("AddGallery")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new AddGalleryFragment()).commit();
                }
            }
        }
    }

    private void getStudentFurtherDetails() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
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
        qualificationSpinner = materialDialog.getCustomView().findViewById(R.id.qualification_spinner);
        satCB=materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB=materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB=materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB=materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB=materialDialog.getCustomView().findViewById(R.id.cv_tofel);

        ArrayAdapter dateAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, dates);
        ArrayAdapter levelAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, qualificationList);

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
                selectedLevel = qualificationList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFurtherDetails();
                materialDialog.dismiss();
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
    private void addFurtherDetails() {



        String studentQualification = qualification.getText().toString();
        String studentInterestedCountry = interestedCountry.getText().toString();
        String studentInterestedCourse = interestedCourse.getText().toString();
        String studentSummary = summary.getText().toString();
        String testsAttended=getTestsString();

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
            String studentCourseCompleted = selectedLevel + ", " + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(studentCourseCompleted, studentInterestedCountry, studentInterestedCourse, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear,testsAttended)
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()){
                                if (response.body() != null){
                                    App.db().putObject(FragmentKeys.DATA, response.body().data);
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                    MDToast mdToast = MDToast.makeText(MainActivity.this, "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                }
                            }else {
                                try {
                                    Log.d("client", "onResponse: error" + response.errorBody().string());
                                    MDToast mdToast = MDToast.makeText(MainActivity.this, "There was something wrong while saving your info. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchFragment()).commit();

                break;

            case R.id.admin_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ConsultancyProfileFragment()).commit();

                break;

            case R.id.student_profile:
                if (data.enquiry_details == null){
                    getStudentFurtherDetails();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new StudentProfileFragment()).commit();
                }
                break;

            case R.id.matched_client:
                if (data.enquiry_details == null){
                    getStudentFurtherDetails();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MatchingClientsFragment()).commit();
                }

                break;

            case R.id.stared:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new InterestedClientsFragment()).commit();

                break;

            case R.id.logout:
                App.logOut(this);
                break;
        }
        drawerLayout.closeDrawers();
    }
}
