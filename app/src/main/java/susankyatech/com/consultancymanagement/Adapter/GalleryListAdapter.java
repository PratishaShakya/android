package susankyatech.com.consultancymanagement.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Model.Gallery;
import susankyatech.com.consultancymanagement.R;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.GalleryViewHolder> {

    private List<String> galleryList;

    public GalleryListAdapter(List<String> galleryList) {
        this.galleryList = galleryList;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_gallery_layout, parent, false);
        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        String image = galleryList.get(position);

        Log.d("asd", "onBindViewHolder: "+image);
        Log.d("asd", "onBindViewHolder: "+holder.galleryImage);

        Picasso.get().load(image).into(holder.galleryImage);

    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageView galleryImage;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            galleryImage = mView.findViewById(R.id.all_gallery_image);
        }
    }
}
