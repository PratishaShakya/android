package susankyatech.com.consultancymanagement.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.R;

public class DocumentUploadListAdapter extends RecyclerView.Adapter<DocumentUploadListAdapter.DocumentUploadViewHolder> {

    public List<String> fileNameList;
    public List<Uri> fineDoneList;
    private Context context;

    public DocumentUploadListAdapter(Context context, List<String> fileNameList, List<Uri> fineDoneList) {
        this.context = context;
        this.fileNameList = fileNameList;
        this.fineDoneList = fineDoneList;
    }

    @NonNull
    @Override
    public DocumentUploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_gallery_image, parent, false);
        return new DocumentUploadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentUploadViewHolder holder, int position) {
        String fileName = fileNameList.get(position);
        holder.fileNameView.setText(fileName);
        Uri image = fineDoneList.get(position);
        Toast.makeText(context, ""+image.getPath(), Toast.LENGTH_SHORT).show();
        holder.fileImageView.setImageURI(image);
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class DocumentUploadViewHolder extends RecyclerView.ViewHolder{

        View mView;
        @BindView(R.id.upload_icon)
        ImageView fileImageView;
        @BindView(R.id.upload_filename)
        TextView fileNameView;
        @BindView(R.id.upload_loading)
        ImageView fileDOneView;

        public DocumentUploadViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            ButterKnife.bind(this, mView);
        }
    }
}
