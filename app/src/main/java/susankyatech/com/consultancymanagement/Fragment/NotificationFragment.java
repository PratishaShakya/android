package susankyatech.com.consultancymanagement.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapters.AppointmentListAdapter;
import susankyatech.com.consultancymanagement.Model.Appointment;
import susankyatech.com.consultancymanagement.R;

public class NotificationFragment extends Fragment {

    @BindView(R.id.appointment_list)
    RecyclerView recyclerView;

    private List<Appointment> appointmentList;
    private AppointmentListAdapter appointmentListAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Appointments");
        init();
        return view;
    }

    private void init() {
        getAppointmentList();

    }

    private void getAppointmentList() {
        appointmentList = new ArrayList<>();
        appointmentList.add(new Appointment("11:00 AM", "25 Sept", "Susankya", "Ram Bahadur"));
        appointmentList.add(new Appointment("1:00 PM", "1 Dec", "Global", "Shyam Bahadur"));
        appointmentListAdapter = new AppointmentListAdapter(getContext(), appointmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(appointmentListAdapter);

    }


}
