package susankyatech.com.consultancymanagement.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import susankyatech.com.consultancymanagement.R;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {

    private List<String> courses;

    public CourseListAdapter(List<String> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_course_layout, viewGroup, false);
        return new CourseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        String course_name = courses.get(position).trim();
        holder.countryName.setText(course_name);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder{
        View mView;

        TextView countryName;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            countryName = mView.findViewById(R.id.course_name);
        }
    }
}
