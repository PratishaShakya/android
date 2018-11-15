package susankyatech.com.consultancymanagement.Adapters;

import android.content.Context;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.List;

import susankyatech.com.consultancymanagement.AdapterDelegates.BannerAdapterDelegate;
import susankyatech.com.consultancymanagement.AdapterDelegates.ConsultancyAdapterDelegate;
import susankyatech.com.consultancymanagement.Interfaces.HomeItems;

public class HomeAdapter extends ListDelegationAdapter<List<HomeItems>> {
    public HomeAdapter(Context activity, List<HomeItems> items) {

        // DelegatesManager is a protected Field in ListDelegationAdapter
        delegatesManager
                .addDelegate(new BannerAdapterDelegate(activity))
                .addDelegate(new ConsultancyAdapterDelegate(activity));


        // Set the items from super class.
        setItems(items);
    }
}
