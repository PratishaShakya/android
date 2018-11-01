package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenInquirySelectCountryFragment extends Fragment {

    @BindView(R.id.country_name)
    EditText countryName;

    public OpenInquirySelectCountryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_open_inquiry_select_country, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        countryName.requestFocus();
    }

}
