package susankyatech.com.consultancymanagement.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haipq.android.flagkit.FlagImageView;

import java.util.List;
import java.util.Locale;

import susankyatech.com.consultancymanagement.R;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.CountryViewHolder> {

    private List<String> countries;

    public CountryListAdapter(List<String> countries) {
        this.countries = countries;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_country_layout, viewGroup, false);
        return new CountryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        String country_name = countries.get(position);
        holder.countryName.setText(country_name);

    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView countryName;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            countryName = mView.findViewById(R.id.country_name);
        }
    }

}
