package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Adapter.EnquiryViewPagerAdapter;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnquiryFragment extends Fragment {

    static TabLayout tabLayout;
    static ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_left_arrow,
            R.drawable.ic_right_arrow,
    };

    public EnquiryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.enquiry_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_enquiry: {
                submitEnquiry();
            }

            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitEnquiry() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enquiry, container, false);
        ButterKnife.bind(this, view);

        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
        init();
        return view;
    }

    private void init() {

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorTransparent));
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        EnquiryViewPagerAdapter adapter = new EnquiryViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new EnquiryPersonalFragment(), "Previous");
        adapter.addFragment(new CourseEnquiryFragment(), "Next");
        Log.d(TAG, "init: "+viewPager.getCurrentItem());;

        viewPager.setAdapter(adapter);
    }

}
