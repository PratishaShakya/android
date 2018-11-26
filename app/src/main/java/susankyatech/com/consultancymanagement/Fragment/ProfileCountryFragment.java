package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.Adapters.CountryListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.Model.ProfileInfo;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileCountryFragment extends Fragment {

    @BindView(R.id.country_list)
    RecyclerView countryList;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;
    @BindView(R.id.btn_add_country)
    FancyButton addCountry;
    @BindView(R.id.message)
    TextView message;

    private int clientId, detail_id;

    private CountryListAdapter countryListAdapter;
    ClientAPI clientAPI;

    NachoTextView wCountry;

    private List<String> countries = new ArrayList<>();
    private List<String> countryLists;

    public ProfileCountryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_country, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        countryList.setVisibility(View.GONE);
        message.setVisibility(View.GONE);

        if (getArguments()!=null){
            clientId = getArguments().getInt("clientId", 0);
        }
        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        countryList.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (clientId == 0){
            getCountryList();
        } else{
            getClientCountryList();
            addCountry.setVisibility(View.GONE);
        }

        addCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                        .title("Edit/Add Countries")
                        .customView(R.layout.add_country_layout, true)
                        .positiveText("Save")
                        .negativeText("Close")
                        .positiveColor(getResources().getColor(R.color.green))
                        .negativeColor(getResources().getColor(R.color.red))
                        .show();

                wCountry = materialDialog.getCustomView().findViewById(R.id.country);
                wCountry.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
                wCountry.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
                wCountry.setText(countries);

                materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        countryLists = new ArrayList<>();
                        countryAdd(materialDialog);
                    }
                });
                materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        materialDialog.dismiss();
                    }
                });
            }
        });
    }

    private void countryAdd(final MaterialDialog materialDialog) {
        for (com.hootsuite.nachos.chip.Chip chip : wCountry.getAllChips()) {
            // Do something with the text of each chip
            CharSequence text = chip.getText();
            countryLists.add(String.valueOf(text));
            Log.d("asd", "onClick: coun" + countryLists.size());

            Client client = App.db().getObject(FragmentKeys.CLIENT, Client.class);
            detail_id = client.detail.id;
            Log.d(TAG, "countryAdd: "+detail_id);
            ProfileInfo clientDetail = new ProfileInfo();
            clientDetail.detail_id = detail_id;
            clientDetail.countries = countryLists;

            clientAPI.addCountry(clientDetail).enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d("asd", "onClick: else success" + response.body().data.countries);
                            getCountryList();
                            materialDialog.dismiss();
                        }
                    } else {
                        try {

                            Log.d("loginError", response.errorBody().string());
                            MDToast mdToast = MDToast.makeText(getContext(), "Error on posting client details. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                            materialDialog.dismiss();
                        } catch (Exception e) {
                        }

                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {

                }
            });
        }
    }

    private void getClientCountryList() {
        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.getSingleClient(ConsultancyProfileFragment.clientStaticID).enqueue(new Callback<Login>() {

            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        if (response.body().data.client.detail.countries != null){
                            countries = response.body().data.client.detail.countries;

                            countryListAdapter = new CountryListAdapter(countries);
                            countryList.setAdapter(countryListAdapter);
                            progressLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            countryList.setVisibility(View.VISIBLE);
                        } else {
                            progressLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            message.setVisibility(View.VISIBLE);
                            message.setText("No Country found");
                        }
                    }
                }else {
                    try {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getActivity(), "Error on getting client details. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: " + t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void getCountryList() {
        clientAPI.getClient().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        countries = response.body().data.client.detail.countries;

                        countryListAdapter = new CountryListAdapter(countries);
                        countryList.setAdapter(countryListAdapter);
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        countryList.setVisibility(View.VISIBLE);
                        addCountry.setVisibility(View.VISIBLE);
                    }
                }else {
                    try {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        Log.d("loginError", response.errorBody().string());
                        MDToast mdToast = MDToast.makeText(getActivity(), "Error on getting client details. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                        mdToast.show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: " + t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

}
