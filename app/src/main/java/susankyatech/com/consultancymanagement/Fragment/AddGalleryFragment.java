package susankyatech.com.consultancymanagement.Fragment;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import susankyatech.com.consultancymanagement.Adapter.ImageUploadListAdapter;
import susankyatech.com.consultancymanagement.R;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

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

    private static final int RESULT_LOAD_IMAGE = 1;

    private List<String> fileNameList;
    private List<String> fileDoneList;
    private ImageUploadListAdapter uploadListAdapter;

    public AddGalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_gallery, container, false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }

    private void init() {

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        uploadListAdapter = new ImageUploadListAdapter(fileNameList, fileDoneList);
        uploadList.setLayoutManager(new LinearLayoutManager(getContext()));
        uploadList.setAdapter(uploadListAdapter);

        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE);
            }
        });

        confirmUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), ""+fileNameList.size(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData() != null){
                int totalItemSelected = data.getClipData().getItemCount();

                for (int i = 0; i < totalItemSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(fileUri);

                    fileNameList.add(fileName);
                    uploadListAdapter.notifyDataSetChanged();
                }
            } else if (data.getData() != null){
                Uri fileUri = data.getData();
                String fileName = getFileName(fileUri);
                fileNameList.add(fileName);
                uploadListAdapter.notifyDataSetChanged();
            }
        }
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
}
