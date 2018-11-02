package susankyatech.com.consultancymanagement.Fragment;


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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
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
import susankyatech.com.consultancymanagement.Adapter.ProfileViewPagerAdapter;
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
    @BindView(R.id.profile_viewpager)
    ViewPager viewPager;
    @BindView(R.id.interest)
    ImageView interest;
    @BindView(R.id.profile_banner)
    ImageView profileBanner;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;
    @BindView(R.id.edit_coverPic)
    ImageView editCoverPic;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    private int clientId;
    private String clientName;
    public static int clientStaticID;

    private File file;

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
        init();
        return view;
    }


    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        profileBanner.setVisibility(View.GONE);
        editCoverPic.setVisibility(View.GONE);


        if (getArguments() != null){
            clientId = getArguments().getInt("client_id", 0);
            clientName = getArguments().getString("client_name");
        }
        clientStaticID=clientId;
        Log.d(TAG, "init: "+clientId);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));


        if (clientId == 0){
            interest.setVisibility(View.GONE);
            getProfileInfo();
        }else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(clientName + "'s Profile");

            getClientProfileInfo();
        }

        interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.db().getBoolean(FragmentKeys.INTERESTED)){
                    setUnInterestInConsultancy();
                }else {
                    setInterestInConsultancy();
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
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
        RequestBody fileBody =
                RequestBody.create( MediaType.parse("multipart/form-data"), file);
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

                    } catch (Exception e) {

                    }
                    if (response.body() != null) {

                    }
                }else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getContext(), "Error on uploading Cover Image. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
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

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE);
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
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        profileBanner.setVisibility(View.VISIBLE);
                        editCoverPic.setVisibility(View.VISIBLE);

                        String imageUrl = response.body().data.client.detail.cover_photo;
                        Picasso.get().load(imageUrl).into(profileBanner);
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
                Log.d("client", "onFailure:tala "+t);
            }
        });
    }

    private void getClientProfileInfo() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        Log.d("OOPS",clientId+"");
        clientAPI.getSingleClient(clientId).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        App.db().putBoolean(FragmentKeys.INTERESTED, response.body().data.client.interested);
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        profileBanner.setVisibility(View.VISIBLE);
                        interest.setVisibility(View.VISIBLE);
                        if (response.body().data.client.detail!=null){

                            String imageUrl = response.body().data.client.detail.cover_photo;
                            Picasso.get().load(imageUrl).into(profileBanner);
                        }else{
                            Picasso.get().load(R.drawable.banner).into(profileBanner);
                        }
                        if (App.db().getBoolean(FragmentKeys.INTERESTED)){
                            interest.setImageResource(R.drawable.ic_interested);
                        }else {
                            interest.setImageResource(R.drawable.ic_interest);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There is no internet connection. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void setUnInterestInConsultancy() {
        ClientInterestAPI clientInterestAPI = App.consultancyRetrofit().create(ClientInterestAPI.class);
        clientInterestAPI.unInterestedOnClient(clientId).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        App.db().putBoolean(FragmentKeys.INTERESTED, false);
                        interest.setImageResource(R.drawable.ic_interest);
                    }
                }else {
                    try {
                        Log.d("interested", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("interested", "onFailure:tala "+t);
            }
        });
    }

    private void setInterestInConsultancy() {
        ClientInterestAPI clientInterestAPI = App.consultancyRetrofit().create(ClientInterestAPI.class);
        clientInterestAPI.interestedOnClient(clientId, 1,0,0).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        App.db().putBoolean(FragmentKeys.INTERESTED, true);
                        interest.setImageResource(R.drawable.ic_interested);
                    }
                }else {
                    try {
                        Log.d("interested", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("interested", "onFailure:tala "+t);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getChildFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putInt("clientId", clientId);

        ProfileInfoFragment profileInfoFragment = new ProfileInfoFragment();
        profileInfoFragment.setArguments(bundle);

        ProfileCountryFragment profileCountryFragment = new ProfileCountryFragment();
        profileCountryFragment.setArguments(bundle);

        ProfileUniversityFragment profileUniversityFragment = new ProfileUniversityFragment();
        profileUniversityFragment.setArguments(bundle);

        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);

        adapter.addFragment(profileInfoFragment, "Info");
        adapter.addFragment(profileCountryFragment, "Countries");
        adapter.addFragment(profileUniversityFragment, "Courses");
        adapter.addFragment(galleryFragment, "Gallery");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

//        setupViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
//
//        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));
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
