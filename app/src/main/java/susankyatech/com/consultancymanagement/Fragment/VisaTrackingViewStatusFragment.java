package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Adapters.StatusListAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Model.Result;
import susankyatech.com.consultancymanagement.Model.Visa;
import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisaTrackingViewStatusFragment extends Fragment {

    @BindView(R.id.name)
    TextView studentName;
    @BindView(R.id.code)
    TextView studentCode;
    @BindView(R.id.dob)
    TextView studentDOB;
    @BindView(R.id.email)
    TextView studentEmail;
    @BindView(R.id.country)
    TextView appliedCountry;
    @BindView(R.id.university)
    TextView appliedUniversity;
    @BindView(R.id.course)
    TextView appliedCourse;
    @BindView(R.id.intake_period)
    TextView intakePeriod;
    @BindView(R.id.status)
    TextView visaStatus;
    @BindView(R.id.statusList)
    RecyclerView recyclerView;

    List<String> statusList = new ArrayList<>();

    private StatusListAdapter adapter;

    Result result;
    Visa visa;

    public VisaTrackingViewStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visa_tracking_view_status, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        result = App.db().getObject(FragmentKeys.RESULT, Result.class);
        visa = result.visa;

        getVisaInfo();
        statusList.add("lol");
        statusList.add("plus");
        statusList.add("rofl");

        adapter = new StatusListAdapter(statusList, getContext(), result.status);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }

    private void getVisaInfo() {
        studentName.setText(result.name);
        studentCode.setText(result.code);
        studentDOB.setText(result.dob);
        studentEmail.setText(result.email);
        appliedCountry.setText(result.country);
        appliedUniversity.setText(visa.university);
        appliedCourse.setText(result.course);
        intakePeriod.setText(visa.intake_period);
        visaStatus.setText(result.status);
    }

}
