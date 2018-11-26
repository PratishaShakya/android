package susankyatech.com.consultancymanagement.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Fragment.ConsultancyProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.OpenInquirySelectCountryFragment;
import susankyatech.com.consultancymanagement.Fragment.SearchFragment;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

public class ConsultancyListAdapter extends RecyclerView.Adapter<ConsultancyListAdapter.ConsultancyListViewHolder> {

    private List<Client> clientList;
    private List<Client> arrayList;
    private Context context;
    private Data data;

    public ConsultancyListAdapter(List<Client> clientList, Context context) {
        this.clientList = clientList;
        this.context = context;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(SearchFragment.clientList);
        this.data = App.db().getObject(FragmentKeys.DATA, Data.class);
    }

    @NonNull
    @Override
    public ConsultancyListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_consultancy_layout, viewGroup, false);
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

        if (clientList.get(i).logo == null){
            Picasso.get().load(R.drawable.banner).into(holder.consultancyLogo);
        }else{
            Picasso.get().load(clientList.get(i).logo).into(holder.consultancyLogo);
        }
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
