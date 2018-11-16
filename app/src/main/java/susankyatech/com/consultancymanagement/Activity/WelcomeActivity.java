package susankyatech.com.consultancymanagement.Activity;

import android.Manifest;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Detail;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.Model.ProfileInfo;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isDownloadsDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isExternalStorageDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isGooglePhotosUri;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isMediaDocument;

public class WelcomeActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_LOGO = 1;

    @BindView(R.id.add_banner)
    ImageView addBanner;
    @BindView(R.id.welcome_location)
    EditText wLocation;
    @BindView(R.id.welcome_phone)
    EditText wPhone;
    @BindView(R.id.welcome_description)
    EditText wDescription;
    @BindView(R.id.btn_save)
    FancyButton save;
    @BindView(R.id.welcome_country)
    NachoTextView wCountry;
    @BindView(R.id.welcome_courses)
    NachoTextView wCourses;
    @BindView(R.id.welcome_established)
    EditText wEstablished;
    @BindView(R.id.welcome_achievements)
    EditText wAchievements;

    private String selectedImagePath;
    private List<String> countryList = new ArrayList<>();
    private List<String> coursesList = new ArrayList<>();
    private File file, logoFile;
    private int FILE_SELECT_CODE = 100, detail_id;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);
        init();

    }

    private void init() {

        wCountry.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        wCountry.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
        wCourses.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        wCourses.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String location = wLocation.getText().toString();
                String phone = wPhone.getText().toString();
                String description = wDescription.getText().toString();
                String established = wEstablished.getText().toString();
                String achievement = wAchievements.getText().toString();

                for (com.hootsuite.nachos.chip.Chip chip : wCountry.getAllChips()) {
                    CharSequence text = chip.getText();
                    countryList.add(String.valueOf(text));
                }

                for (com.hootsuite.nachos.chip.Chip chip : wCourses.getAllChips()) {
                    CharSequence text = chip.getText();
                    coursesList.add(String.valueOf(text));
                }

                if (selectedImagePath == null) {
                    Toast.makeText(WelcomeActivity.this, "Upload Image", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(location)) {
                    wLocation.setError("Enter Location");
                    wLocation.requestFocus();
                } else if (TextUtils.isEmpty(phone)) {
                    wPhone.setError("Enter Location");
                    wPhone.requestFocus();
                } else if (coursesList.size() == 0) {
                    wCourses.setError("Enter course");
                    wCourses.requestFocus();
                } else if (countryList.size() == 0) {
                    wCountry.setError("Enter course");
                    wCountry.requestFocus();
                } else if (TextUtils.isEmpty(description)) {
                    wDescription.setError("Enter Location");
                    wDescription.requestFocus();
                } else if (TextUtils.isEmpty(established)) {
                    wEstablished.setError("Enter Location");
                    wEstablished.requestFocus();
                } else if (TextUtils.isEmpty(achievement)) {
                    wAchievements.setError("Enter Location");
                    wAchievements.requestFocus();
                } else {
                    addClientDetail(location, phone, description, established, achievement);
                }
            }
        });

        addBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Cover Picture"), RESULT_LOAD_IMAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void addClientDetail(String location, String phone, String description, String established, String achievement) {

        client = App.db().getObject(FragmentKeys.CLIENT, Client.class);

        ProfileInfo clientDetail = new ProfileInfo();
        if (client.detail != null){
            detail_id = client.detail.detail_id;
            clientDetail.detail_id = detail_id;
        }
        clientDetail.courses = coursesList;
        clientDetail.countries = countryList;
        clientDetail.description = description;
        clientDetail.phone = phone;
        clientDetail.location = location;
        clientDetail.achievements = achievement;
        clientDetail.established = established;

        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.addClient(clientDetail).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(@NonNull Call<Login> call, @NonNull Response<Login> response) {

                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        uploadCoverPic();
                    }
                } else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Error on posting client details. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(@NonNull Call<Login> call, @NonNull Throwable t) {
                MDToast mdToast = MDToast.makeText(getApplicationContext(), "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void uploadCoverPic() {
        RequestBody fileBody =
                RequestBody.create( MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("cover_photo", file.getName(), fileBody);

        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);

        clientAPI.addCoverPicture(fileToUpload).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(@NonNull Call<Login> call, @NonNull Response<Login> response) {

                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getApplicationContext(), "Error on posting cover picture. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(@NonNull Call<Login> call, @NonNull Throwable t) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == RESULT_LOAD_IMAGE) && (resultCode == -1)) {
            selectedImagePath = getPath(data.getData());
            file = new File(getPath(data.getData()));
            addBanner.setImageURI(data.getData());
        }
    }

    public String getPath(Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(getApplicationContext(), contentUri, null, null);
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

                return getDataColumn(getApplicationContext(), contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(getApplicationContext(), uri, null, null);
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

}
