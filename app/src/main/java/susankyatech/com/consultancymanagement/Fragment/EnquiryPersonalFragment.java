package susankyatech.com.consultancymanagement.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.R;


public class EnquiryPersonalFragment extends Fragment {

    @BindView(R.id.enquiry_name)
    EditText ename;
    @BindView(R.id.enquiry_address)
    EditText eaddress;
    @BindView(R.id.enquiry_dob)
    EditText edob;
    @BindView(R.id.enquiry_phone)
    EditText ephone;
    @BindView(R.id.enquiry_email)
    EditText eemail;

    private boolean isValidate;

    public EnquiryPersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enquiry_personal, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        validate();
        if (isValidate){
            EnquiryFragment.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 1) {
                        EnquiryFragment.viewPager.setCurrentItem(0);
                        TabLayout.Tab tab1 = EnquiryFragment.tabLayout.getTabAt(0);
                        tab1.select();
                    } else {
                        Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    private boolean validate() {
        String name = ename.getText().toString();
        String address = eaddress.getText().toString();
        String dob = edob.getText().toString();
        String phone = ephone.getText().toString();
        String email = eemail.getText().toString();

        if (TextUtils.isEmpty(name)){
            ename.setError("Enter your name");
            ename.requestFocus();
        } else if (TextUtils.isEmpty(address)){
            ename.setError("Enter your name");
            ename.requestFocus();
        } else if (TextUtils.isEmpty(dob)){
            ename.setError("Enter your name");
            ename.requestFocus();
        } else if (TextUtils.isEmpty(phone)){
            ename.setError("Enter your name");
            ename.requestFocus();
        } else if (TextUtils.isEmpty(email)){
            ename.setError("Enter your name");
            ename.requestFocus();
        } else{
            return isValidate = true;
        }
        return isValidate = false;

    }


}
