package susankyatech.com.consultancymanagement.AdapterDelegates;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapters.ConsultancyListAdapter;
import susankyatech.com.consultancymanagement.Decorations.HorizontalSpaceItemDecoration;
import susankyatech.com.consultancymanagement.Decorations.VerticalSpaceItemDecoration;
import susankyatech.com.consultancymanagement.Fragment.ConsultancyProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.OpenInquirySelectCountryFragment;
import susankyatech.com.consultancymanagement.Interfaces.HomeItems;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.ConsultancyGrid;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.R;

public class ConsultancyAdapterDelegate  extends AdapterDelegate<List<HomeItems>> {

    private LayoutInflater inflater;
    private Context activity;

    public ConsultancyAdapterDelegate(Context activity) {
        inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.activity = activity;
    }

    @Override
    protected boolean isForViewType(@NonNull List<HomeItems> items, int position) {
        return items.get(position) instanceof ConsultancyGrid;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new ConsultancyListViewHolder(inflater.inflate(R.layout.layout_grid, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull List<HomeItems> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        ConsultancyListViewHolder vh = (ConsultancyListViewHolder) holder;
        ConsultancyGrid consultancySlide = (ConsultancyGrid) items.get(position);

      vh.gridLayoutManager = new GridLayoutManager(activity, 2);
      vh.recyclerView.setLayoutManager(vh.gridLayoutManager);
      vh.recyclerView.setAdapter(new ConsultancyListAdapter(consultancySlide.clientList, activity));
      vh.recyclerView.addItemDecoration(new com.susankya.wcbookstore.ItemDecorations.GridViewItemDecoration(activity));
    }

    public class ConsultancyListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.consultancy_list)
        RecyclerView recyclerView;
        GridLayoutManager gridLayoutManager;

        public ConsultancyListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
