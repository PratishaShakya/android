package susankyatech.com.consultancymanagement.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapters.ImageSliderAdapter;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

public class GalleryFullScreenFragment extends Fragment {

//    @BindView(R.id.full_screen_gallery)
//    ImageView fullScreen;
    @BindView(R.id.galleryViewPager)
    ViewPager mSlidePager;



    private String galleryImage;
    private int adapterPosition;

    private ArrayList<String> galleryList = new ArrayList<>();

    private ImageSliderAdapter sliderAdapter;

    public GalleryFullScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery_full_screen, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).getSupportActionBar().hide();
        init();
        return view;
    }

    private void init() {
        if (getArguments() != null){
            galleryImage = getArguments().getString("image");
            galleryList = getArguments().getStringArrayList("imageList");
            adapterPosition = getArguments().getInt("position");
        }
//        Picasso.get().load(galleryImage).into(fullScreen);

        sliderAdapter = new ImageSliderAdapter(getActivity(), galleryList);

        Log.d(TAG, "init: "+adapterPosition);
        mSlidePager.setAdapter(sliderAdapter);
        mSlidePager.setCurrentItem(adapterPosition);
    }






}
