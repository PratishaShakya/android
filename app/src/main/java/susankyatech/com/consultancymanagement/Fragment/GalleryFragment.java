package susankyatech.com.consultancymanagement.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.GalleryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapter.GalleryListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
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
    @BindView(R.id.btn_add_gallery)
    FancyButton addGallery;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;

    GalleryListAdapter galleryListAdapter;
    List<Gallery> allGallery = new ArrayList<>();
    ArrayList<String> images;

    private int clientId;



    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this,view);
        ((MainActivity)getActivity()).getSupportActionBar().show();
        init();
        return view;
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        galleryList.setVisibility(View.GONE);
        if (getArguments()!=null){
            clientId = getArguments().getInt("clientId", 0);
        }

        if (clientId != 0){
            addGallery.setVisibility(View.GONE);
            listClientGallery();
        }else {
            listGallery();
        }
        allGallery = new ArrayList<>();

        addGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra(FragmentKeys.FRAGMENTNAME, "AddGallery");
                startActivity(intent);
            }
        });


        galleryList.setLayoutManager(new GridLayoutManager(getContext(),2));


    }

    private void listClientGallery() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.getSingleClient(clientId).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        App.db().putBoolean(FragmentKeys.INTERESTED, response.body().data.client.interested);
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        galleryList.setVisibility(View.VISIBLE);

                        allGallery = response.body().data.client.galleries;
                        images = new ArrayList<>();
                        for(int i=0;i<allGallery.size();i++)
                            images.add(allGallery.get(i).image);

                        Log.d("asd", "onResponse: "+allGallery.size());
                        galleryListAdapter = new GalleryListAdapter(allGallery, images,getContext());
                        galleryList.setAdapter(galleryListAdapter);
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

    private void listGallery() {
        GalleryAPI galleryAPI = App.consultancyRetrofit().create(GalleryAPI.class);
        galleryAPI.getGalleries().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        Log.d("asd", "onResponse: "+response.body().data);
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        galleryList.setVisibility(View.VISIBLE);
                        addGallery.setVisibility(View.VISIBLE);

                        allGallery = response.body().data.client.galleries;
                        images = new ArrayList<>();
                        for(int i=0;i<allGallery.size();i++)
                        images.add(allGallery.get(i).image);

                        Log.d("asd", "onResponse: "+allGallery.size());
                        galleryListAdapter = new GalleryListAdapter(allGallery, images,getContext());
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
