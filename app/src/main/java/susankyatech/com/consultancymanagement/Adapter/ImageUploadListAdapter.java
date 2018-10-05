package susankyatech.com.consultancymanagement.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.R;

public class ImageUploadListAdapter extends RecyclerView.Adapter<ImageUploadListAdapter.ImageUploadViewHolder> {

    public List<String> fileNameList;
    public List<Uri> fineDoneList;

    public ImageUploadListAdapter(List<String> fileNameList, List<Uri> fineDoneList) {
        this.fileNameList = fileNameList;
        this.fineDoneList = fineDoneList;
    }

    @NonNull
    @Override
    public ImageUploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_gallery_image, parent, false);
        return new ImageUploadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageUploadViewHolder holder, int position) {

        String fileName = fileNameList.get(position);
        holder.fileNameView.setText(fileName);
        Uri image = fineDoneList.get(position);
        holder.fileImageView.setImageURI(image);
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ImageUploadViewHolder extends RecyclerView.ViewHolder{

        View mView;
        @BindView(R.id.upload_icon)
        ImageView fileImageView;
        @BindView(R.id.upload_filename)
        TextView fileNameView;
        @BindView(R.id.upload_loading)
        ImageView fileDOneView;

        public ImageUploadViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            ButterKnife.bind(this, mView);
        }
    }
}
