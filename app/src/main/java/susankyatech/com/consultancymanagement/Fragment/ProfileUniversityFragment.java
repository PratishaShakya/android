package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileUniversityFragment extends Fragment {


    public ProfileUniversityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_university, container, false);
    }

}
