package susankyatech.com.consultancymanagement.Fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.ClientInterestAPI;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapters.ProfileViewPagerAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isDownloadsDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isExternalStorageDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isGooglePhotosUri;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isMediaDocument;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsultancyProfileFragment extends Fragment {

    @BindView(R.id.profile_tabs)
    TabLayout tabLayout;
    public static ViewPager viewPager;
    @BindView(R.id.profile_banner)
    ImageView profileBanner;
    @BindView(R.id.edit_coverPic)
    ImageView editCoverPic;
    @BindView(R.id.sendInquiry)
    RelativeLayout sendInquiry;
    @BindView(R.id.show_map)
    RelativeLayout showMap;
    @BindView(R.id.open_inquiry)
    RelativeLayout openInquiry;
    @BindView(R.id.relative1)
    RelativeLayout relativeLayout;
    @BindView(R.id.consultancy_name)
    TextView consultancyName;
    @BindView(R.id.consultancy_logo)
    CircleImageView consultancyLogo;
    @BindView(R.id.consultancy_email)
    TextView location;

    private Data data;

    private EditText qualification, summary, userName, userEmail, userAddress, userPhone, year, month, day;
    private Spinner completedYear, qualificationSpinner;
    private CheckBox ieltsCB, toeflCB, greCB, pteCB, satCB;

    ArrayAdapter dateAdapter, levelAdapter;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    private int clientId, selectedYear;
    private String clientName, selected_options, selectedLevel;;
    public static int clientStaticID;

    List<Integer> dates = new ArrayList<>();
    String[] qualificationList = {"+2", "Bachelors", "Masters"};

    private File file;
    private ProgressDialog progressDialog;

    public ConsultancyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        viewPager = view.findViewById(R.id.profile_viewpager);
        init();
        return view;
    }

    private void init() {
        profileBanner.setVisibility(View.GONE);
        editCoverPic.setVisibility(View.GONE);
        openInquiry.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getContext());

        if (getArguments() != null) {
            clientId = getArguments().getInt("client_id", 0);
            clientName = getArguments().getString("client_name");
        }
        clientStaticID = clientId;
        Log.d(TAG, "init: " + clientId);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue));

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = todayYear; i > 1969; i--) {
            dates.add(i);
        }

        dateAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, dates);
        levelAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, qualificationList);

        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (clientId == 0) {
            sendInquiry.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            getProfileInfo();
        } else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(clientName + "'s Profile");
            setHasOptionsMenu(true);
            getClientProfileInfo();
        }

        sendInquiry.setOnClickListener(new View.OnClickListener() {
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

        editCoverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
                    } else {
                        openFilePicker();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    return;
                } else {
                    sendUserToMapFragment();
                }

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

    private void getEnquiry() {
        Bundle bundle = new Bundle();
        bundle.putInt("client_id", clientId);
        bundle.putString("client_name", clientName);

        FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
        OpenInquirySelectCountryFragment openInquirySelectCountryFragment = new OpenInquirySelectCountryFragment();
        openInquirySelectCountryFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.main_container, openInquirySelectCountryFragment).addToBackStack(null).commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        inflater.inflate(R.menu.chat_menu, menu);
        Log.d(TAG, "onCreateOptionsMenu: " + clientId);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat:
                Bundle bundle = new Bundle();
                bundle.putInt("client_id", clientId);

                FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.main_container, chatFragment).addToBackStack(null).commit();
                break;
        }
        return true;
    }

    private void sendUserToMapFragment() {
        FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
        ShowMapFragment showMapFragment = new ShowMapFragment();
        fragmentTransaction.replace(R.id.main_container, showMapFragment).addToBackStack(null).commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                sendUserToMapFragment();
            }
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == RESULT_LOAD_IMAGE) && (resultCode == -1)) {
            String fileName = getPath(data.getData());
            file = new File(getPath(data.getData()));
            uploadCoverPic();
        }
    }

    private void uploadCoverPic() {
        progressDialog.setTitle("Uploading Cover Image");
        progressDialog.setMessage("Please wait, while we are uploading your cover image.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RequestBody fileBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("cover_photo", file.getName(), fileBody);

        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.addCoverPicture(fileToUpload).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    try {
                        MDToast mdToast = MDToast.makeText(getContext(), "Cover Image Successfully uploaded!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                        mdToast.show();
                        getProfileInfo();
                        progressDialog.dismiss();

                    } catch (Exception e) {

                    }
                    if (response.body() != null) {

                    }
                } else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getContext(), "Error on uploading Cover Image. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                        progressDialog.dismiss();
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    public String getPath(Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(getContext(), uri)) {
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
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                Log.d(TAG, "getPath:q " + uri);

                return getDataColumn(getContext(), contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    Log.d(TAG, "getPath:w " + uri);
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(getContext(), contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(getContext(), uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Log.d(TAG, "getPath: " + uri);
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

    private void getProfileInfo() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.getClient().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        App.db().putObject(FragmentKeys.CLIENT, response.body().data.client);
                        profileBanner.setVisibility(View.VISIBLE);
                        editCoverPic.setVisibility(View.VISIBLE);

                        String imageUrl = response.body().data.client.detail.cover_photo;
                        String logoUrl = response.body().data.client.logo;
                        Picasso.get().load(imageUrl).into(profileBanner);
                        Picasso.get().load(logoUrl).into(consultancyLogo);

                        consultancyName.setText(response.body().data.client.client_name);
                        location.setText(response.body().data.client.detail.location);

                    }
                } else {
                    try {
                        Log.d("client", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("client", "onFailure:tala " + t);
            }
        });
    }

    private void getClientProfileInfo() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        Log.d("OOPS", clientId + "");
        clientAPI.getSingleClient(clientId).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        App.db().putBoolean(FragmentKeys.INTERESTED, response.body().data.client.interested);
                        profileBanner.setVisibility(View.VISIBLE);
                        sendInquiry.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);


                        if (response.body().data.client.detail != null) {
                            String imageUrl = response.body().data.client.detail.cover_photo;
                            String logoUrl = response.body().data.client.logo;

                            Picasso.get().load(imageUrl).into(profileBanner);
                            Picasso.get().load(logoUrl).into(consultancyLogo);
                            consultancyName.setText(response.body().data.client.client_name);
                            location.setText(response.body().data.client.detail.location);
                        } else {
                            Picasso.get().load(R.drawable.banner).into(profileBanner);
                        }
//                        if (App.db().getBoolean(FragmentKeys.INTERESTED)) {
//                            interest.setImageResource(R.drawable.ic_interested);
//                        } else {
//                            interest.setImageResource(R.drawable.ic_interest);
//                        }
                    }

                }else {
                    try {
                        Log.d("client", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There is no internet connection. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getChildFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putInt("clientId", clientId);
        bundle.putString("clientName", clientName);

        ProfileInfoFragment profileInfoFragment = new ProfileInfoFragment();
        profileInfoFragment.setArguments(bundle);

        ProfileCountryFragment profileCountryFragment = new ProfileCountryFragment();
        profileCountryFragment.setArguments(bundle);

        ProfileCourseFragment profileCourseFragment = new ProfileCourseFragment();
        profileCourseFragment.setArguments(bundle);

        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);

        adapter.addFragment(profileInfoFragment, "Info");
        adapter.addFragment(profileCountryFragment, "Countries");
        adapter.addFragment(profileCourseFragment, "Courses");
        adapter.addFragment(galleryFragment, "Gallery");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public static ConsultancyProfileFragment newInstance(int client_id, String client_name) {
        ConsultancyProfileFragment consultancyProfileFragment = new ConsultancyProfileFragment();
        Bundle args = new Bundle();
        args.putInt("client_id", client_id);
        args.putString("client_name", client_name);
        consultancyProfileFragment.setArguments(args);
        return consultancyProfileFragment;
    }
}
