package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.GalleryAPI;
import susankyatech.com.consultancymanagement.Adapter.GalleryListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Model.Gallery;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    @BindView(R.id.gallery_list)
    RecyclerView galleryList;

    GalleryListAdapter galleryListAdapter;
    List<Gallery> allGallery = new ArrayList<>();



    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }

    private void init() {
        allGallery = new ArrayList<>();

        galleryList.setLayoutManager(new GridLayoutManager(getContext(),2));

        listGallery();
    }

    private void listGallery() {
        GalleryAPI galleryAPI = App.consultancyRetrofit().create(GalleryAPI.class);
        galleryAPI.getGalleries().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        Log.d("asd", "onResponse: "+response.body().data);
                        allGallery = response.body().data.client.galleries;
                        Log.d("asd", "onResponse: "+allGallery.size());
                        galleryListAdapter = new GalleryListAdapter(allGallery, getContext());
                        galleryList.setAdapter(galleryListAdapter);
                    }
                }else {
                    try {
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getActivity(), "Error on getting gallery. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });


    }


}
