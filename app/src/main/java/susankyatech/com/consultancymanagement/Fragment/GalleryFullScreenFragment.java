package susankyatech.com.consultancymanagement.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.R;

public class GalleryFullScreenFragment extends Fragment {

    @BindView(R.id.full_screen_gallery)
    ImageView fullScreen;

    private String galleryImage;

    public GalleryFullScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery_full_screen, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        if (getArguments() != null){
            galleryImage = getArguments().getString("image");
        }
        Picasso.get().load(galleryImage).into(fullScreen);
    }


}
