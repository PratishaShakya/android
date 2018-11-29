package susankyatech.com.consultancymanagement.Fragment;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.LoginActivity;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
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
    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.btn_edit)
    FancyButton editInfo;
    @BindView(R.id.upload_file)
    RelativeLayout uploadFile;
    @BindView(R.id.profile_pic)
    CircleImageView profilePic;
    @BindView(R.id.change_picture)
    ImageView changePicture;


    private EditText qualification, summary, userName, userEmail, userAddress, userPhone, year, month, day;
    private Spinner completedYear, qualificationSpinner;
    private CheckBox ieltsCB, toeflCB, greCB, pteCB, satCB;

    List<Integer> dates = new ArrayList<>();
    String[] qualificationList = {"+2", "Bachelors", "Masters"};
    private String testAttended, selectedLevel;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private int maxLength = 500 * 1024;

    List<String> testsAttendedList = new ArrayList<>();
    private int selectedYear, mYear, mMonth, mDay;
    ;

    private EnquiryDetails enquiryDetails;
    private File file;
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
        data = App.db().getObject(FragmentKeys.DATA, Data.class);
        if (data.enquiry_details == null) {
            getStudentFurtherDetails();
        } else {
            getStudentInfo();
        }

        profilePic.setVisibility(View.GONE);
        changePicture.setVisibility(View.GONE);

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = todayYear; i > 1969; i--) {
            dates.add(i);
        }

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editStudentDetails();
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                UploadFilesFragment uploadFilesFragment = new UploadFilesFragment();
                fragmentTransaction.replace(R.id.main_container, uploadFilesFragment).addToBackStack(null).commit();
            }
        });

        changePicture.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
        }

    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == RESULT_LOAD_IMAGE) && (resultCode == -1)  && data!=null) {
            String fileName = getPath(data.getData());
            Log.d("lkj", "onActivityRseult: "+data);
            file = new File(getPath(data.getData()));
            uploadProfilePic();
        }
    }

    private void uploadProfilePic() {
        if (file.length() > maxLength) {
            MDToast mdToast = MDToast.makeText(getActivity(), "Image size exceeded 500 KB!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
            mdToast.show();
        }
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
                        Uri.parse("content://downloads/my_downloads"), Long.valueOf(id));
                Log.d("lkj", "onActivityRsesult:ss "+uri);

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
        Log.d("lkj", "onActivityRsesult:ss1 "+uri);
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            Log.d("lkj", "onActivityRsesult:ss2 "+uri);
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


    private void getStudentInfo() {
        data = App.db().getObject(FragmentKeys.DATA, Data.class);
        enquiryDetails = data.enquiry_details;
        if (enquiryDetails.qualification != null){
            qualificationTv.setText(enquiryDetails.qualification.get(0) + ", " + enquiryDetails.qualification.get(1));
        }
        nameTv.setText(data.name);
        addressTv.setText(data.address);
        contactTv.setText(data.phone);
        emailIdTv.setText(data.email);
        summaryTv.setText(enquiryDetails.summary);
        completeYear.setText(enquiryDetails.completed_year);
        testAttendedTv.setText(enquiryDetails.test_attended);
        if (data.dob != null) {
            dob.setText(data.dob);
        }
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
        qualificationSpinner = materialDialog.getCustomView().findViewById(R.id.qualification_spinner);
        userName = materialDialog.getCustomView().findViewById(R.id.enquiry_name);
        userAddress = materialDialog.getCustomView().findViewById(R.id.enquiry_address);
        userEmail = materialDialog.getCustomView().findViewById(R.id.enquiry_email);
        userPhone = materialDialog.getCustomView().findViewById(R.id.enquiry_phone);
        satCB = materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB = materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB = materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB = materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB = materialDialog.getCustomView().findViewById(R.id.cv_tofel);

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
        year = materialDialog.getCustomView().findViewById(R.id.year);
        month = materialDialog.getCustomView().findViewById(R.id.month);
        day = materialDialog.getCustomView().findViewById(R.id.day);
        satCB = materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB = materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB = materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB = materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB = materialDialog.getCustomView().findViewById(R.id.cv_tofel);


        ArrayAdapter dateAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, dates);
        ArrayAdapter levelAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, qualificationList);

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
        for (int i = 0; i < qualificationList.length; i++) {
            if (qualificationList[i].equals(datafromApi)) {
                qualificationSpinner.setSelection(i);
            }
        }

        enquiryDetails = data.enquiry_details;
        Log.d(TAG, "editStudentDetails: " + enquiryDetails.qualification.get(0));
        qualification.setText(enquiryDetails.qualification.get(1));
        summary.setText(enquiryDetails.summary);
        completeYear.setText(enquiryDetails.completed_year);
        testAttendedTv.setText(enquiryDetails.test_attended);
        userEmail.setText(data.email);
        userName.setText(data.name);
        userPhone.setText(data.phone);
        userAddress.setText(data.address);
        if (data.dob != null) {
//            userDOB.setText(data.dob);

        }





        String[] testsSplit = enquiryDetails.test_attended.split(",");

        for (int i = 0; i < testsSplit.length; i++) {
            testsSplit[i] = testsSplit[i].trim();
        }

        Collections.addAll(testsAttendedList, testsSplit);

        CheckBox[] checkBoxes = new CheckBox[]{ieltsCB, toeflCB, satCB, pteCB, greCB};

        for (int i = 0; i < checkBoxes.length; i++) {
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

        if (TextUtils.isEmpty(studentSummary)) {
            summary.setError("Enter Summary");
            summary.requestFocus();
        } else if (TextUtils.isEmpty(yrs)) {
            year.setError("Enter year");
            year.requestFocus();
        } else if (TextUtils.isEmpty(mth)) {
            month.setError("Enter month");
            month.requestFocus();
        } else if (TextUtils.isEmpty(days)) {
            day.setError("Enter day");
            day.requestFocus();
        } else if (TextUtils.isEmpty(studentQualification)) {
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)) {
            summary.setError("Enter your qualification");
            summary.requestFocus();
        } else {
            String studentDOB = yrs + "-" + mth + "-" + days;
            String userQualification = selectedLevel + "," + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(userQualification, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear, testsAttended)
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
                                    MDToast mdToast = MDToast.makeText(getContext(), "There was something wrong while saving your info. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                                    mdToast.show();
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
                                materialDialog.dismiss();
                                MDToast mdToast = MDToast.makeText(getContext(), "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                mdToast.show();
                                Log.d(TAG, "onResponse: " + response.body().data.name);
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
