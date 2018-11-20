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
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import susankyatech.com.consultancymanagement.API.GalleryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapters.ImageUploadListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.R;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isDownloadsDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isExternalStorageDocument;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isGooglePhotosUri;
import static susankyatech.com.consultancymanagement.Generic.FileURI.isMediaDocument;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGalleryFragment extends Fragment {

    @BindView(R.id.select_btn)
    ImageButton mSelectBtn;
    @BindView(R.id.upload_list)
    RecyclerView uploadList;
    @BindView(R.id.confirm_upload_image)
    FancyButton confirmUpload;
    @BindView(R.id.main_heading)
    TextView mainHeading;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_FILE = 2;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private int maxLength = 2048 * 1024;

    private String fragmentName, filePath;

    private List<String> fileNameList;
    private List<Uri> fileImageList;
    private ImageUploadListAdapter uploadListAdapter;
    private File file;
    private List<File> fileGalleryList;

    public AddGalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_gallery, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        if (getArguments() != null) {
            fragmentName = getArguments().getString(FragmentKeys.FRAGMENTNAME);
            Log.d(TAG, "init: " + fragmentName);
        }


        if (fragmentName.equals("studentProfile")) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Add Documents");
            mainHeading.setText("Upload Your Documents");
        } else if (fragmentName.equals("MainActivity")){
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Add Gallery");
        }

        fileNameList = new ArrayList<>();
        fileImageList = new ArrayList<>();
        fileGalleryList = new ArrayList<>();

        uploadListAdapter = new ImageUploadListAdapter(getContext(), fileNameList, fileImageList);
        uploadList.setLayoutManager(new LinearLayoutManager(getContext()));
        uploadList.setAdapter(uploadListAdapter);

        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentName.equals("studentProfile")) {
                    selectFiles();
                } else if (fragmentName.equals("MainActivity")){
                    selectImage();
                }

            }
        });

        confirmUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadGallery();
            }
        });
    }

    private void selectFiles() {
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

    private void selectImage() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
            } else {
                openImagePicker();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (fragmentName.equals("studentProfile")) {
                openFilePicker();
            } else {
                openImagePicker();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), RESULT_LOAD_FILE);
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    private void uploadGallery() {

        for (int i = 0; i < fileGalleryList.size(); i++) {
            if (fileGalleryList.get(i).length() > maxLength) {
                MDToast mdToast = MDToast.makeText(getActivity(), "Image size exceeded 2 MB!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        }

        if (fileGalleryList.size() == 0) {
            MDToast mdToast = MDToast.makeText(getActivity(), "Select atleast 1 image!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
            mdToast.show();
        } else {
            MultipartBody.Part[] files = new MultipartBody.Part[fileGalleryList.size()];
            for (int i = 0; i < fileGalleryList.size(); i++) {
                RequestBody fileBody =
                        RequestBody.create(MediaType.parse("multipart/form-data"), fileGalleryList.get(i));
                files[i] = MultipartBody.Part.createFormData("images[" + i + "]", fileGalleryList.get(i).getName(), fileBody);

            }

            //Setting the file name as an empty string here causes the same issue, which is sending the request successfully without saving the files in the backend, so don't neglect the file name parameter.

            Log.d("loginError1", "filelength" + files.length);
            GalleryAPI galleryAPI = App.consultancyRetrofit().create(GalleryAPI.class);
            galleryAPI.addGalleries(files).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            Log.d("loginError1", response.body().string() + "");

                        } catch (Exception e) {

                        }
                        if (response.body() != null) {
                            Fragment fragment = new ConsultancyProfileFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.main_container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    } else {
                        try {
                            Log.d("loginError", response.errorBody().string());
                            MDToast mdToast = MDToast.makeText(getActivity(), "Error on getting gallery. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                        } catch (Exception e) {
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    MDToast mdToast = MDToast.makeText(getActivity(), "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                    mdToast.show();
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                if (data.getClipData() != null) {
                    int totalItemSelected = data.getClipData().getItemCount();

                    for (int i = 0; i < totalItemSelected; i++) {
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        String fileName = getFileName(fileUri);
                        file = new File(getPath(data.getData()));
                        fileGalleryList.add(file);
                        fileNameList.add(fileName);
                        fileImageList.add(data.getData());
                        uploadListAdapter.notifyDataSetChanged();
                    }
                } else if (data.getData() != null) {
                    Uri fileUri = data.getData();
                    String fileName = getFileName(fileUri);
                    file = new File(getPath(fileUri));

//                    Uri uri = data.getData();
//                    String fileName = getFileName(uri);
//                    File file = new File(uri.getPath());//create path from uri
//                    final String[] split = file.getPath().split(":");//split the path.
//                    filePath = split[1];//assign it to a string(your choice).

                    fileGalleryList.add(file);
                    fileNameList.add(fileName);
                    fileImageList.add(data.getData());
                    uploadListAdapter.notifyDataSetChanged();

                }
            } else if (requestCode == RESULT_LOAD_FILE) {
                if (data.getClipData() != null) {
                    int totalItemSelected = data.getClipData().getItemCount();

                    for (int i = 0; i < totalItemSelected; i++) {
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        String fileName = getFileName(fileUri);
                        file = new File(getPath(data.getData()));
                        fileGalleryList.add(file);
                        fileNameList.add(fileName);
                        fileImageList.add(data.getData());
                        uploadListAdapter.notifyDataSetChanged();
                    }
                } else if (data.getData() != null) {
//                    Uri fileUri = data.getData();
//                    String fileName = getFileName(fileUri);
//                    Log.d("poi1", "onActivityResult: "+fileUri + ", " + fileName);
//                    file = new File(getPath(fileUri));
//                    Log.d("poi1", "onActivityResult: "+file);

                    Uri uri = data.getData();
                    String fileName = getFileName(uri);
                    File file = new File(uri.getPath());//create path from uri
                    final String[] split = file.getPath().split(":");//split the path.
                    filePath = split[1];//assign it to a string(your choice).


                    fileGalleryList.add(file);
                    fileNameList.add(fileName);
                    fileImageList.add(data.getData());
                    uploadListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public String getPath(Uri uri) {
        Log.d("poi1", "getPath:3 "+uri);
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(getActivity(), uri)) {
            Log.d("poi1", "getPath:4 "+uri);
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Log.d("poi1", "getPath:1 "+uri);
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                Log.d("poi1", "getPath: "+uri);
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/my_downloads"), Long.valueOf(id));
                Log.d("poi1", "getPath: "+contentUri);
                return getDataColumn(getActivity(), contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Log.d("poi1", "getPath:2 "+uri);
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

                return getDataColumn(getActivity(), contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            Log.d("poi1", "getPath: "+uri);

            return getDataColumn(getActivity(), uri, null, null);
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


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public Boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents" == uri.getAuthority();
    }


    public Boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents" == uri.getAuthority();
    }


    public Boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents" == uri.getAuthority();
    }

    public Boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content" == uri.getAuthority();
    }
}
