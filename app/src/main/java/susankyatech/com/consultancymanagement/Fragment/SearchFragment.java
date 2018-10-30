package susankyatech.com.consultancymanagement.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapter.ConsultancyListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Decorations.HorizontalSpaceItemDecoration;
import susankyatech.com.consultancymanagement.Decorations.VerticalSpaceItemDecoration;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MenuItem.OnMenuItemClickListener{

    @BindView(R.id.consultancy_list)
    RecyclerView recyclerView;
    @BindView(R.id.search_options)
    Spinner spinner;
    @BindView(R.id.searchCountry)
    SearchView searchCountry;
    @BindView(R.id.searchConsultancy)
    SearchView searchConsultancy;
    @BindView(R.id.searchCourse)
    SearchView searchCourse;
    @BindView(R.id.reset)
    ImageView reset;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;
    @BindView(R.id.whole_layout)
    RelativeLayout wholeLayout;

    View view;
    private ClientAPI clientAPI;

    private Context context;

    String[] options = { "Consultancy", "Course", "Country"};
    private String selected_options;

    ConsultancyListAdapter consultancyListAdapter;

    public static List<Client> clientList;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this,view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Search");
        init();
        return view;
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wholeLayout.setVisibility(View.GONE);
        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);



        ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item, options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_options = options[i];
                switch (selected_options){
                    case "Consultancy":
                        searchCourse.setVisibility(View.GONE);
                        searchConsultancy.setVisibility(View.VISIBLE);
                        searchCountry.setVisibility(View.GONE);
                        break;
                    case "Course":
                        searchCourse.setVisibility(View.VISIBLE);
                        searchConsultancy.setVisibility(View.GONE);
                        searchCountry.setVisibility(View.GONE);
                        break;
                    case "Country":
                        searchCourse.setVisibility(View.GONE);
                        searchConsultancy.setVisibility(View.GONE);
                        searchCountry.setVisibility(View.VISIBLE);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getAllConsultancy();

        searchConsultancy.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String text = s;
                consultancyListAdapter.filter(text);
                return false;
            }
        });

        searchCourse.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                clientAPI.searchByCourse(s).enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null){
                                clientList = response.body().data.clients;
                                consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                                recyclerView.setAdapter(consultancyListAdapter);

                            }
                        }else {
                            try {
                                Log.d("client", "onResponse: error" + response.errorBody().string());
                                MDToast mdToast = MDToast.makeText(getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                                mdToast.show();
                            } catch (Exception e) {
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchCountry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                progressLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                wholeLayout.setVisibility(View.GONE);
                clientAPI.searchByCountry(s).enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null){
                                progressLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                wholeLayout.setVisibility(View.VISIBLE);
                                clientList = response.body().data.clients;
                                consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());

                                recyclerView.setAdapter(consultancyListAdapter);

                            }
                        }else {
                            try {
                                Log.d("client", "onResponse: error" + response.errorBody().string());
                                MDToast mdToast = MDToast.makeText(getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                                mdToast.show();
                            } catch (Exception e) {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSearch();
            }
        });

    }

    private void resetSearch() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wholeLayout.setVisibility(View.GONE);
        clientAPI.getAllClients().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        wholeLayout.setVisibility(View.VISIBLE);
                        clientList = response.body().data.clients;
                        consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        recyclerView.setAdapter(consultancyListAdapter);

                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }

    private void getAllConsultancy() {
        clientAPI.getAllClients().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        wholeLayout.setVisibility(View.VISIBLE);
                        clientList = response.body().data.clients;
                        consultancyListAdapter =  new ConsultancyListAdapter(clientList, getContext());
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        recyclerView.setAdapter(consultancyListAdapter);
                        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(16));
                        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16));

                    }

                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
