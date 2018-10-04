package susankyatech.com.consultancymanagement.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PartMap;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Model.Detail;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

public class WelcomeActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;

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
    private File file;


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

                for (com.hootsuite.nachos.chip.Chip chip: wCountry.getAllChips()) {
                    // Do something with the text of each chip
                    CharSequence text = chip.getText();
                    countryList.add(String.valueOf(text));
                    Log.d("asd", "onClick: coun"+countryList.size());
                }

                for (com.hootsuite.nachos.chip.Chip chip: wCourses.getAllChips()) {
                    // Do something with the text of each chip
                    CharSequence text = chip.getText();
                    coursesList.add(String.valueOf(text));
                    Log.d("asd", "onClick: cour"+coursesList.size());
                }

                if (selectedImagePath == null){
                    Toast.makeText(WelcomeActivity.this, "Upload Image", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(location)){
                    wLocation.setError("Enter Location");
                    wLocation.requestFocus();
                } else if (TextUtils.isEmpty(phone)){
                    wPhone.setError("Enter Location");
                    wPhone.requestFocus();
                } else if (coursesList.size() == 0){
                    wCourses.setError("Enter course");
                    wCourses.requestFocus();
                } else if (countryList.size() == 0){
                    wCountry.setError("Enter course");
                    wCountry.requestFocus();
                } else if (TextUtils.isEmpty(description)){
                    wDescription.setError("Enter Location");
                    wDescription.requestFocus();
                } else if (TextUtils.isEmpty(established)){
                    wEstablished.setError("Enter Location");
                    wEstablished.requestFocus();
                } else if (TextUtils.isEmpty(achievement)){
                    wAchievements.setError("Enter Location");
                    wAchievements.requestFocus();
                } else {
                    sendClientDetail(location, phone, description, established, achievement);
                    Log.d("asd", "onClick: else"+phone);
                }



            }
        });

        addBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });


    }

    private void sendClientDetail(String location, String phone, String description, String established, String achievement) {
        Log.d("asd", "onClick: else"+phone);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        RequestBody achievements = RequestBody.create(MediaType.parse("text/plain"),achievement);
        RequestBody detailId = RequestBody.create(MediaType.parse("text/plain"),"1");
        RequestBody rLocation = RequestBody.create(MediaType.parse("text/plain"),location);
        RequestBody rPhone = RequestBody.create(MediaType.parse("text/plain"),phone);
//        RequestBody rDescription = RequestBody.create(MediaType.parse("text/plain"),description);
//        RequestBody rCountires = RequestBody.create(MediaType.parse("text/plain"),countryList);
        final RequestBody rCourse = RequestBody.create(MediaType.parse("text/plain"),achievement);

        Detail clientDetail = new Detail();
        Map<String, RequestBody > detail = new HashMap();
        detail.put("achievements", achievements);
        detail.put("detail_id", detailId);
        detail.put("location", rLocation);
        detail.put("phone", rPhone);
//        detail.put("description", rDescription);
//        detail.put("countries", rCountires);
        detail.put("course", rCourse);

        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.addClientDetail(body, detail).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        Log.d("asd", "onClick: else"+rCourse);
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            Log.d("loginError", response.errorBody().string());
                            MDToast mdToast = MDToast.makeText(getApplicationContext(), "Email address and password doesn't match. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                        } catch (Exception e) {
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                MDToast mdToast = MDToast.makeText(getApplicationContext(), "There is no internet connection. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
//                file = new File(selectedImagePath);
                addBanner.setImageURI(selectedImageUri);
                Log.d("asd", "onActivityResult: "+selectedImagePath);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        Log.d(TAG, "getPath: "+cursor);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        Log.d(TAG, "getPath: "+columnIndex);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        Log.d(TAG, "getPath: "+filePath);
        return filePath;
    }

}
