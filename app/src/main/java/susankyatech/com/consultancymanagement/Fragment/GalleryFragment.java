package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Adapter.GalleryListAdapter;
import susankyatech.com.consultancymanagement.Model.Gallery;
import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    @BindView(R.id.gallery_list)
    RecyclerView galleryList;

    GalleryListAdapter galleryListAdapter;
    List<String> allGallery;



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

        galleryList.setLayoutManager(new GridLayoutManager(getContext(),3));

        listGallery();
    }

    private void listGallery() {
        allGallery.add("https://i.pinimg.com/originals/e1/c1/19/e1c119f2df282d53b15dd5a31f10696f.jpg");
        allGallery.add("https://i.pinimg.com/originals/b1/d5/dc/b1d5dc447dcfa29244ed6b24c962e36f.jpg");
        galleryListAdapter = new GalleryListAdapter(allGallery);
        galleryList.setAdapter(galleryListAdapter);

    }


}
