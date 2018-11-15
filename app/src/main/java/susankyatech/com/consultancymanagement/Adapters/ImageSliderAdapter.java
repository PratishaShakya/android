package susankyatech.com.consultancymanagement.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import susankyatech.com.consultancymanagement.R;


public class ImageSliderAdapter extends PagerAdapter {

    private Context context;
    LayoutInflater layoutInflater;
    List<String> images = new ArrayList<>();

    public ImageSliderAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_slide_layout, container, false);

        ZoomageView mImageView = view.findViewById(R.id.user_post_image);
        Picasso.get().load(images.get(position)).into(mImageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
