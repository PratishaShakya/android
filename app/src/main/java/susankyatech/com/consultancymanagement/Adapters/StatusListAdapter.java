package susankyatech.com.consultancymanagement.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Decorations.VectorDrawableUtils;
import susankyatech.com.consultancymanagement.R;

import static susankyatech.com.consultancymanagement.R.*;

public class StatusListAdapter extends RecyclerView.Adapter<StatusListAdapter.StatusListViewHolder> {

    private List<String> statusList;
    private Context context;
    private String status;

    public StatusListAdapter(List<String> statusList, Context context, String status) {
        this.statusList = statusList;
        this.context = context;
        this.status = status;
    }

    @NonNull
    @Override
    public StatusListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(layout.item_timeline, viewGroup, false);
        return new StatusListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusListViewHolder holder, int i) {
        if (statusList.get(i).equals(status)){
            Log.d("asd", "onBindViewHolder: "+i);
            for (int j = 0; j < i; j++){
                Log.d("asd", "onBindViewHolder:i "+i);
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, drawable.ic_marker_active, color.colorTransparentPrimary));
            }
            holder.cardView.setCardBackgroundColor(Color.rgb(112,193,169));
            holder.mMessage.setTextColor(Color.WHITE);
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, drawable.ic_marker_active, color.colorPrimary));
        }else {
            holder.cardView.setCardBackgroundColor(Color.rgb(242,242,242));
            holder.mMessage.setTextColor(Color.BLACK);
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, drawable.ic_marker_inactive, android.R.color.darker_gray));
        }
        holder.mMessage.setText(statusList.get(i));

    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class StatusListViewHolder extends RecyclerView.ViewHolder {

        @BindView(id.card)
        CardView cardView;
        @BindView(id.text_timeline_title)
        TextView mMessage;
        @BindView(id.time_marker)
        TimelineView mTimelineView;

        public StatusListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
