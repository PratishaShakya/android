package susankyatech.com.consultancymanagement.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Model.Appointment;
import susankyatech.com.consultancymanagement.R;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.AppointmentListViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;

    public AppointmentListAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_appointment_layout, viewGroup, false);
        return new AppointmentListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListViewHolder holder, int position) {
        holder.appointmentDate.setText(appointmentList.get(position).appointmentDate);
        holder.appointmentTime.setText(appointmentList.get(position).appointmentTime);
        holder.counselorName.setText(appointmentList.get(position).counselorName);
        holder.consultancyName.setText(appointmentList.get(position).consultancyName);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class AppointmentListViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.appointment_date)
        TextView appointmentDate;
        @BindView(R.id.appointment_time)
        TextView appointmentTime;
        @BindView(R.id.consultancyName)
        TextView consultancyName;
        @BindView(R.id.counselorName)
        TextView counselorName;

        public AppointmentListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
