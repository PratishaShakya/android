package susankyatech.com.consultancymanagement.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
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
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.BannerAPI;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Fragment.AddGalleryFragment;
import susankyatech.com.consultancymanagement.Fragment.ConsultancyProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.InterestedClientsFragment;
import susankyatech.com.consultancymanagement.Fragment.MatchingClientsFragment;
import susankyatech.com.consultancymanagement.Fragment.SearchFragment;
import susankyatech.com.consultancymanagement.Fragment.StudentProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.VisaTrackingFragment;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isDownloadsDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isExternalStorageDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isGooglePhotosUri;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isMediaDocument;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawable_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_app_bar)
    Toolbar mToolbar;

    private EditText qualification, summary, userName, userEmail, userAddress, userPhone, year, month, day;

    private Spinner completedYear, qualificationSpinner;
    private String fragmentName, selectedLevel;
    private int selectedYear, mYear, mMonth, mDay;

    private CheckBox ieltsCB, toeflCB, greCB, pteCB, satCB;

    private ProgressDialog progressDialog;

    List<Integer> dates = new ArrayList<>();
    private String[] qualificationList = {
            "+2",
            "Bachelors",
            "Masters"
    };

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    private File file;
    private Data data;
    private Client client;
    CircleImageView userLogo;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        checkAppRelease();
    }

    private void checkAppRelease() {
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Log.d("Latest Version", update.getLatestVersion());
                        Log.d("available", isUpdateAvailable + "");
                        App.db().putString(Keys.VERSION, update.getLatestVersion());
                        if (isUpdateAvailable) {
                            App.db().putString(Keys.VERSION, update.getLatestVersion());
                            showUpdateDialog();
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                    }
                });
        appUpdaterUtils.start();

    }

    private void showUpdateDialog() {
        new AppUpdater(this)
                .setContentOnUpdateAvailable("Check out the latest version available of Consultancy Finder!")
                .setButtonUpdate("Update now?")
                .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppStore();
                    }
                })
                .setButtonDismiss("Maybe later")
                .setButtonDismissClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButtonDoNotShowAgain("Huh, not interested")
                .setButtonDoNotShowAgainClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        App.db().putBoolean(Keys.NOT_INTERESTED, true);
                    }
                })
                .setCancelable(false)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true)
                .start();
    }

    private void openAppStore() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void init() {

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Consultancy Manager");
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        navigationView.setItemIconTintList(null);

        progressDialog = new ProgressDialog(this);

        for (int i = todayYear; i > 1969; i--) {
            dates.add(i);
        }

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if (App.db().getBoolean(Keys.IS_STUDENT)) {
            navigationView.inflateMenu(R.menu.navigation_menu_student);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchFragment()).commit();
            data = App.db().getObject(FragmentKeys.DATA, Data.class);
            Log.d("poi", "init: " + data);
            View navView = navigationView.inflateHeaderView(R.layout.nav_header_layout);
            ImageView navBg = navView.findViewById(R.id.nav_background);
            getNavBackground(navBg);

            TextView userName = navView.findViewById(R.id.user_name);
            userName.setText(data.name);

            if (data.enquiry_details == null) {
                getStudentFurtherDetails();
            } else {
                if (data.dob == null) {
                    getDOB();
                }
            }
        } else {
            navigationView.inflateMenu(R.menu.navigation_menu_admin);

            client = App.db().getObject(FragmentKeys.CLIENT, Client.class);
            View navView = navigationView.inflateHeaderView(R.layout.nav_header_admin);
            ImageView navBg = navView.findViewById(R.id.nav_background);
            getNavBackground(navBg);
            TextView userName = navView.findViewById(R.id.user_name);
            TextView userEmail = navView.findViewById(R.id.user_email);
            userLogo = navView.findViewById(R.id.client_logo);
            ImageView editLogo = navView.findViewById(R.id.add_logo);

            editLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
                        } else {
                            openFilePicker();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            Picasso.get().load(client.logo).placeholder(R.drawable.banner).into(userLogo);
            userName.setText(client.client_name);
            userEmail.setText(data.email);

            if (getIntent() != null) {
                fragmentName = getIntent().getStringExtra(FragmentKeys.FRAGMENTNAME);
                if (fragmentName == null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ConsultancyProfileFragment()).commit();
                } else if (fragmentName.equals("AddGallery")) {
                    AddGalleryFragment addGalleryFragment = new AddGalleryFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, addGalleryFragment).commit();
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
    }


    private void getNavBackground(ImageView navBg) {
        BannerAPI bannerAPI = App.consultancyRetrofit().create(BannerAPI.class);
        bannerAPI.getNavBanner().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().data == null) {
                            Picasso.get().load(R.drawable.nav_background).into(navBg);
                        } else {
                            String nav_image = response.body().data.ad_image;
                            Picasso.get().load(nav_image).into(navBg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
        }

    }

    public void updateViews() {
        client = App.db().getObject(FragmentKeys.CLIENT, Client.class);
        Picasso.get().load(client.logo).placeholder(R.drawable.banner).into(userLogo);
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Logo"), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("asd", "onActivityResult: "+data);

        if ((requestCode == RESULT_LOAD_IMAGE) && (resultCode == -1)) {
            Log.d("asd", "onActivityResult0: "+data);
            String fileName = getPath(data.getData());
            file = new File(getPath(data.getData()));
            uploadLogo();
        }
    }

    private void uploadLogo() {
        progressDialog.setTitle("Uploading Logo");
        progressDialog.setMessage("Please wait, while we are uploading your logo.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RequestBody fileBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("logo", file.getName(), fileBody);

        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.addLogo(fileToUpload).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d("loginError1", response.body().string() + "");
                        getProfileInfo();
                        MDToast mdToast = MDToast.makeText(MainActivity.this, "Logo Successfully uploaded!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                        mdToast.show();
                        progressDialog.dismiss();


                    } catch (Exception e) {

                    }
                    if (response.body() != null) {

                    }
                } else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(MainActivity.this, "Error on uploading logo. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                        progressDialog.dismiss();
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                MDToast mdToast = MDToast.makeText(MainActivity.this, "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void getProfileInfo() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.getClient().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        App.db().putObject(FragmentKeys.CLIENT, response.body().data.client);
                        updateViews();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }

    public String getPath(Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/my_downloads"), Long.valueOf(id));

                return getDataColumn(this, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private void getDOB() {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("Add your Date Of Birth")
                .customView(R.layout.add_dob_layout, true)
                .positiveText("Save Details")
                .negativeText("Close")
                .positiveColor(getResources().getColor(R.color.green))
                .negativeColor(getResources().getColor(R.color.red))
                .show();

        year = materialDialog.getCustomView().findViewById(R.id.year);
        month = materialDialog.getCustomView().findViewById(R.id.month);
        day = materialDialog.getCustomView().findViewById(R.id.day);

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudentDOB();
            }
        });
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
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
        ArrayAdapter dateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dates);
        ArrayAdapter levelAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, qualificationList);


        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        userEmail.setText(data.email);
        userName.setText(data.name);
        userPhone.setText(data.phone);
        userAddress.setText(data.address);

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

    private String getTestsString() {
        List<String> chosenTests = new ArrayList<>();
        String tests = "";
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

        for (int i = 0; i < chosenTests.size(); i++) {
            String test = chosenTests.get(i);
            if (i == chosenTests.size() - 1)
                tests += test;
            else tests += test + ", ";
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
        String yrs = year.getText().toString();
        String mth = month.getText().toString();
        String days = day.getText().toString();


        String testsAttended = getTestsString();

        if (TextUtils.isEmpty(studentQualification)) {
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)) {
            summary.setError("Enter Summary");
            summary.requestFocus();
        } else if (TextUtils.isEmpty(yrs)){
            year.setError("Enter year");
            year.requestFocus();
        } else if (TextUtils.isEmpty(mth)){
            month.setError("Enter month");
            month.requestFocus();
        } else if (TextUtils.isEmpty(yrs)){
            day.setError("Enter day");
            day.requestFocus();
        } else  {
            String studentDOB = yrs + "-" + mth + "-" + days;
            String studentCourseCompleted = selectedLevel + ", " + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(studentCourseCompleted, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear, testsAttended)
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    editStudentPrimaryInfo(studentName, studentEmail, studentAddress, studentPhone, materialDialog, studentDOB);

                                }
                            } else {
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

    private void addStudentDOB() {
        String yrs = year.getText().toString();
        String mth = month.getText().toString();
        String days = day.getText().toString();

        if (TextUtils.isEmpty(yrs)){
            year.setError("Enter year");
            year.requestFocus();
        } else if (TextUtils.isEmpty(mth)){
            month.setError("Enter month");
            month.requestFocus();
        } else if (TextUtils.isEmpty(yrs)){
            day.setError("Enter day");
            day.requestFocus();
        } else {
            String studendDOB = yrs + "-" + mth + "-" + days;
            ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
            clientAPI.addDOB(studendDOB).enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            App.db().putObject(FragmentKeys.DATA, response.body().data);
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            MDToast mdToast = MDToast.makeText(MainActivity.this, "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                            mdToast.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {

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
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                App.db().putObject(FragmentKeys.DATA, response.body().data);
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                MDToast mdToast = MDToast.makeText(MainActivity.this, "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                mdToast.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {

                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchFragment()).commit();

                break;

            case R.id.admin_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ConsultancyProfileFragment()).commit();

                break;

            case R.id.student_profile:
                if (data.enquiry_details == null) {
                    getStudentFurtherDetails();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new StudentProfileFragment()).commit();
                }
                break;

            case R.id.visa_track:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new VisaTrackingFragment()).commit();
                break;

            case R.id.matched_client:
                if (data.enquiry_details == null) {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        userMenuSelector(item);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
