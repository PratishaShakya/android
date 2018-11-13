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
import susankyatech.com.consultancymanagement.Adapter.CountryListAdapter;
import susankyatech.com.consultancymanagement.Adapter.CourseListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Detail;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.Model.ProfileInfo;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileUniversityFragment extends Fragment {

    @BindView(R.id.course_list)
    RecyclerView courseList;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;
    @BindView(R.id.btn_add_course)
    FancyButton addCourse;
    @BindView(R.id.message)
    TextView message;

    private int clientId, detail_id;

    private CourseListAdapter courseListAdapter;
    ClientAPI clientAPI;

    NachoTextView wCourse;

    private List<String> courses = new ArrayList<>();
    private List<String> coursesList;

    public ProfileUniversityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_university, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        courseList.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        if (getArguments()!=null){
            clientId = getArguments().getInt("clientId", 0);
        }
        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        courseList.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (clientId == 0){
            getCourseList();
        } else{
            getClientCourseList();
            addCourse.setVisibility(View.GONE);
        }

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                        .title("Edit/Add Course")
                        .customView(R.layout.add_course_layout, true)
                        .positiveText("Save")
                        .negativeText("Close")
                        .positiveColor(getResources().getColor(R.color.green))
                        .negativeColor(getResources().getColor(R.color.red))
                        .show();

                wCourse = materialDialog.getCustomView().findViewById(R.id.course);
                wCourse.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
                wCourse.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
                wCourse.setText(courses);

                materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        coursesList = new ArrayList<>();
                        courseAdd(materialDialog);
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

    private void courseAdd(final MaterialDialog materialDialog) {
        for (com.hootsuite.nachos.chip.Chip chip : wCourse.getAllChips()) {
            // Do something with the text of each chip
            CharSequence text = chip.getText();
            coursesList.add(String.valueOf(text));
            Log.d(TAG, "countryAdd: "+detail_id);

            Client client = App.db().getObject(FragmentKeys.CLIENT, Client.class);
            detail_id = client.detail.id;
            ProfileInfo clientDetail = new ProfileInfo();
            clientDetail.detail_id = detail_id;
            clientDetail.courses = coursesList;

            clientAPI.addCourse(clientDetail).enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d("asd", "onClick: else success" );
                            getCourseList();
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

    private void getClientCourseList() {
        clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        clientAPI.getSingleClient(ConsultancyProfileFragment.clientStaticID).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        courses = response.body().data.client.detail.courses;

                        courseListAdapter = new CourseListAdapter(courses);
                        courseList.setAdapter(courseListAdapter);
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        courseList.setVisibility(View.VISIBLE);
                    }
                }else {
                    try {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
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
                Log.d(TAG, "onFailure: " + t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

    private void getCourseList() {
        clientAPI.getClient().enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        courses = response.body().data.client.detail.courses;

                        courseListAdapter = new CourseListAdapter(courses);
                        courseList.setAdapter(courseListAdapter);
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        courseList.setVisibility(View.VISIBLE);
                        addCourse.setVisibility(View.VISIBLE);
                    }
                }else {
                    try {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
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
                Log.d(TAG, "onFailure: " + t.getMessage());
                MDToast mdToast = MDToast.makeText(getActivity(), "There was problem trying to connect to network. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                mdToast.show();
            }
        });
    }

}
