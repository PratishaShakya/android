package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.API.ClientInterestAPI;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapter.ProfileViewPagerAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsultancyProfileFragment extends Fragment {

    @BindView(R.id.profile_tabs)
    TabLayout tabLayout;
    @BindView(R.id.profile_viewpager)
    ViewPager viewPager;
    @BindView(R.id.interest)
    ImageView interest;
    @BindView(R.id.profile_banner)
    ImageView profileBanner;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;


    private int clientId;
    private String clientName;
    public static int clientStaticID;

    public ConsultancyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        init();
        return view;
    }


    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        profileBanner.setVisibility(View.GONE);

        if (getArguments() != null){
            clientId = getArguments().getInt("client_id", 0);
            clientName = getArguments().getString("client_name");
        }
        clientStaticID=clientId;
        Log.d(TAG, "init: "+clientId);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));


        if (clientId == 0){
            interest.setVisibility(View.GONE);
            getProfileInfo();
        }else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(clientName + "'s Profile");
            getClientProfileInfo();
        }

        interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.db().getBoolean(FragmentKeys.INTERESTED)){
                    setUnInterestInConsultancy();
                }else {
                    setInterestInConsultancy();
                }
            }
        });


    }

    private void getProfileInfo() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.getClient().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        profileBanner.setVisibility(View.VISIBLE);

                        String imageUrl = response.body().data.client.detail.cover_photo;
                        Picasso.get().load(imageUrl).into(profileBanner);
                    }
                } else {
                    try {
                        Log.d("client", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("client", "onFailure:tala "+t);
            }
        });
    }

    private void getClientProfileInfo() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        Log.d("OOPS",clientId+"");
        clientAPI.getSingleClient(clientId).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        App.db().putBoolean(FragmentKeys.INTERESTED, response.body().data.client.interested);
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        profileBanner.setVisibility(View.VISIBLE);
                        interest.setVisibility(View.VISIBLE);
                        if (response.body().data.client.detail!=null){

                            String imageUrl = response.body().data.client.detail.cover_photo;
                            Picasso.get().load(imageUrl).into(profileBanner);
                        }else{
                            Picasso.get().load(R.drawable.banner).into(profileBanner);
                        }
                        if (App.db().getBoolean(FragmentKeys.INTERESTED)){
                            interest.setImageResource(R.drawable.ic_interested);
                        }else {
                            interest.setImageResource(R.drawable.ic_interest);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There is no internet connection. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void setUnInterestInConsultancy() {
        ClientInterestAPI clientInterestAPI = App.consultancyRetrofit().create(ClientInterestAPI.class);
        clientInterestAPI.unInterestedOnClient(clientId).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        App.db().putBoolean(FragmentKeys.INTERESTED, false);
                        interest.setImageResource(R.drawable.ic_interest);
                    }
                }else {
                    try {
                        Log.d("interested", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("interested", "onFailure:tala "+t);
            }
        });
    }

    private void setInterestInConsultancy() {
        ClientInterestAPI clientInterestAPI = App.consultancyRetrofit().create(ClientInterestAPI.class);
        clientInterestAPI.interestedOnClient(clientId).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        App.db().putBoolean(FragmentKeys.INTERESTED, true);
                        interest.setImageResource(R.drawable.ic_interested);
                    }
                }else {
                    try {
                        Log.d("interested", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("interested", "onFailure:tala "+t);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getChildFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putInt("clientId", clientId);

        ProfileInfoFragment profileInfoFragment = new ProfileInfoFragment();
        profileInfoFragment.setArguments(bundle);

        ProfileCountryFragment profileCountryFragment = new ProfileCountryFragment();
        profileCountryFragment.setArguments(bundle);

        ProfileUniversityFragment profileUniversityFragment = new ProfileUniversityFragment();
        profileUniversityFragment.setArguments(bundle);

        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);

        adapter.addFragment(profileInfoFragment, "Info");
        adapter.addFragment(profileCountryFragment, "Countries");
        adapter.addFragment(profileUniversityFragment, "Courses");
        adapter.addFragment(galleryFragment, "Gallery");
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

    public static ConsultancyProfileFragment newInstance(int client_id, String client_name) {
        ConsultancyProfileFragment consultancyProfileFragment = new ConsultancyProfileFragment();
        Bundle args = new Bundle();
        args.putInt("client_id", client_id);
        args.putString("client_name", client_name);
        consultancyProfileFragment.setArguments(args);
        return consultancyProfileFragment;
    }
}
