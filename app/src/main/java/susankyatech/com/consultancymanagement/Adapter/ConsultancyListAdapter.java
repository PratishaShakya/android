package susankyatech.com.consultancymanagement.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Fragment.ConsultancyProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.EnquiryFragment;
import susankyatech.com.consultancymanagement.Fragment.SearchFragment;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.R;

public class ConsultancyListAdapter extends RecyclerView.Adapter<ConsultancyListAdapter.ConsultancyListViewHolder> {

    private List<Client> clientList;
    private List<Client> arrayList;
    private Context context;

    public ConsultancyListAdapter(List<Client> clientList, Context context) {
        this.clientList = clientList;
        this.context = context;
        this.arrayList = new ArrayList<Client>();
        this.arrayList.addAll(SearchFragment.clientList);
    }

    @NonNull
    @Override
    public ConsultancyListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_consultancy_layout, viewGroup, false);
        return new ConsultancyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultancyListViewHolder holder, final int i) {
        holder.consultancyName.setText(clientList.get(i).client_name);
        holder.wholeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                ConsultancyProfileFragment consultancyProfileFragment = ConsultancyProfileFragment.newInstance(clientList.get(i).id, clientList.get(i).client_name);
                fragmentTransaction.replace(R.id.main_container, consultancyProfileFragment).addToBackStack(null).commit();
            }
        });
//        Picasso.get().load(clientList.get(i).detail.cover_photo).into(holder.consultancyLogo);
        holder.btnEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("client_id", clientList.get(i).id);
                bundle.putString("client_name", clientList.get(i).client_name);
//                bundle.putString("client_location", clientList.get(i).detail.location);

                EnquiryFragment enquiryFragment = new EnquiryFragment();
                enquiryFragment.setArguments(bundle);
                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, enquiryFragment).addToBackStack(null).commit();
            }
        });
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        SearchFragment.clientList.clear();
        if (charText.length() == 0) {
            SearchFragment.clientList.addAll(arrayList);
        } else {
            for (Client wp : arrayList) {
                if (wp.client_name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    SearchFragment.clientList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public class ConsultancyListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.consultancy_logo)
        ImageView consultancyLogo;
        @BindView(R.id.btn_enquiry)
        FancyButton btnEnquiry;
        @BindView(R.id.consultancy_name)
        TextView consultancyName;
        @BindView(R.id.consultancy_whole_layout)
        CardView wholeLayout;

        public ConsultancyListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
