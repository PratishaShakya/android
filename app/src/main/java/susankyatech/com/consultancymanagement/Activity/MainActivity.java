package susankyatech.com.consultancymanagement.Activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

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

    private Spinner completedYear;
    private String fragmentName;

    private int selectedYear;
    List<Integer> dates = new ArrayList<>();

    private Data data;

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

        for (int i = todayYear; i > 1969; i--){
            dates.add(i);
        }

//        View navView = navigationView.inflateHeaderView(R.layout.nav_header_layout);

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
            getSupportActionBar().setTitle("Hi "+data.name + ",");
            Log.d("poi", "init: "+data);
            if (data.enquiry_details == null){

                getStudentFurtherDetails();
            }
        }
        else {
            navigationView.inflateMenu(R.menu.navigation_menu_admin);
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

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,dates);
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

    private void addFurtherDetails() {
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
        }  else if (TextUtils.isEmpty(studentSummary)){
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
                                    MDToast mdToast = MDToast.makeText(MainActivity.this, "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
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

            case R.id.add_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new AddGalleryFragment()).commit();

                break;

            case R.id.list_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new GalleryFragment()).commit();

                break;

            case R.id.logout:
                App.logOut(this);
                break;
        }
        drawerLayout.closeDrawers();
    }
}
