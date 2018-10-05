package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Adapter.EnquiryViewPagerAdapter;
import susankyatech.com.consultancymanagement.Application.MySpannable;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_tabs)
    TabLayout tabLayout;
    @BindView(R.id.profile_viewpager)
    ViewPager viewPager;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }


    private void init() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));
        viewPager.setCurrentItem(0);
    }

    private void setupViewPager(ViewPager viewPager) {
        EnquiryViewPagerAdapter adapter = new EnquiryViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new ProfileInfoFragment(), "Info");
        adapter.addFragment(new ProfileCountryFragment(), "Country");
        adapter.addFragment(new ProfileUniversityFragment(), "Courses");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

//        setupViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
//
//        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));
    }
}
