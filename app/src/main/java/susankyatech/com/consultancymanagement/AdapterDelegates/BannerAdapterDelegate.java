package susankyatech.com.consultancymanagement.AdapterDelegates;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Interfaces.HomeItems;
import susankyatech.com.consultancymanagement.Model.BannerGrid;
import susankyatech.com.consultancymanagement.R;

import static android.support.constraint.Constraints.TAG;

public class BannerAdapterDelegate extends AdapterDelegate<List<HomeItems>> {

    private LayoutInflater inflater;
    private Context activity;

    public BannerAdapterDelegate(Context activity) {
        inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.activity = activity;
    }

    @Override
    protected boolean isForViewType(@NonNull List<HomeItems> items, int position) {
        return items.get(position) instanceof BannerGrid;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new BannerViewHolder(inflater.inflate(R.layout.banner_slide_layout, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull List<HomeItems> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        BannerViewHolder vh = (BannerViewHolder) holder;
        BannerGrid bannerGrid = (BannerGrid) items.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.fitCenter();

        Log.d("hey", "onBindViewHolder: "+bannerGrid.bannerItemList.size());
        if(bannerGrid.bannerItemList.size() == 0){
            vh.sliderLayout.setVisibility(View.GONE);
        }

        for (int i = 0; i < bannerGrid.bannerItemList.size(); i++) {
            DefaultSliderView sliderView = new DefaultSliderView(activity);
            sliderView
                    .image(bannerGrid.bannerItemList.get(i).banner)
                    .setRequestOption(requestOptions)
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true);

            vh.sliderLayout.addSlider(sliderView);
        }
        // set Slider Transition Animation
        vh.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        vh.sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        vh.sliderLayout.setCustomAnimation(new DescriptionAnimation());
        vh.sliderLayout.setDuration(4000);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sliderLayout)
        SliderLayout sliderLayout;


        public BannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
