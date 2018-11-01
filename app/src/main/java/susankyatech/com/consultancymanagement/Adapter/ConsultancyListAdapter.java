package susankyatech.com.consultancymanagement.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Fragment.ConsultancyProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.SearchFragment;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Generic.Keys;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.EnquiryDetails;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

import static android.content.ContentValues.TAG;

public class ConsultancyListAdapter extends RecyclerView.Adapter<ConsultancyListAdapter.ConsultancyListViewHolder> {

    private List<Client> clientList;
    private List<Client> arrayList;
    private Context context;
    private Data data;
    private EnquiryDetails enquiryDetails;
    private int selectedYear;

    List<Integer> dates = new ArrayList<>();

    private EditText qualification, interestedCountry, interestedCourse, summary;
    private TextView qualificationTv, completeYearTv, interestCourseTv, destination, testAttendedTv, summaryTv;

    private Spinner completedYear;

    public ConsultancyListAdapter(List<Client> clientList, Context context) {
        this.clientList = clientList;
        this.context = context;
        this.arrayList = new ArrayList<Client>();
        this.arrayList.addAll(SearchFragment.clientList);
        this.data = App.db().getObject(FragmentKeys.DATA, Data.class);

        int todayYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = todayYear; i > 1969; i--) {
            dates.add(i);
        }
    }

    @NonNull
    @Override
    public ConsultancyListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_consultancy_layout, viewGroup, false);
        return new ConsultancyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultancyListViewHolder holder, final int i) {
        holder.consultancyName.setText(clientList.get(i).client_name);
        holder.wholeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                ConsultancyProfileFragment consultancyProfileFragment = ConsultancyProfileFragment.newInstance(clientList.get(i).id, clientList.get(i).client_name);
                fragmentTransaction.replace(R.id.main_container, consultancyProfileFragment).addToBackStack(null).commit();
            }
        });

        Picasso.get().load(clientList.get(i).detail.cover_photo).into(holder.consultancyLogo);
        holder.btnEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putInt("client_id", clientList.get(i).id);
                bundle.putString("client_name", clientList.get(i).client_name);
//                bundle.putString("client_location", clientList.get(i).detail.location);

                if (data.enquiry_details == null) {
                    getStudentFurtherDetails(clientList.get(i).id, clientList.get(i).client_name);
                } else {
                    getEnquiry(clientList.get(i).id, clientList.get(i).client_name);
//                    EnquiryFragment enquiryFragment = new EnquiryFragment();
//                    enquiryFragment.setArguments(bundle);
//                    ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, enquiryFragment).addToBackStack(null).commit();
                }

            }
        });
    }

    private void getEnquiry(final int id, final String client_name) {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("Inquiring To " + client_name)
                .customView(R.layout.fragment_enquiry, true)
                .positiveText("Send Details")
                .negativeText("Edit Profile")
                .positiveColor(context.getResources().getColor(R.color.green))
                .negativeColor(context.getResources().getColor(R.color.blue))
                .show();

        qualificationTv = materialDialog.getCustomView().findViewById(R.id.qualification);
        completeYearTv = materialDialog.getCustomView().findViewById(R.id.complete_year);
        interestCourseTv = materialDialog.getCustomView().findViewById(R.id.destination);
        destination = materialDialog.getCustomView().findViewById(R.id.interested_course);
        testAttendedTv = materialDialog.getCustomView().findViewById(R.id.test_attended);
        summaryTv = materialDialog.getCustomView().findViewById(R.id.summary);

        getStudentInfo();

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editStudentDetails(id, client_name);
                materialDialog.dismiss();
            }
        });

    }

    private void editStudentDetails(final int id, final String client_name) {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("Edit Your Profile")
                .customView(R.layout.fragment_course_enquiry, true)
                .positiveText("Save Details")
                .negativeText("Close")
                .positiveColor(context.getResources().getColor(R.color.green))
                .negativeColor(context.getResources().getColor(R.color.red))
                .show();

        qualification = materialDialog.getCustomView().findViewById(R.id.enquiry_level_completed);
        completedYear = materialDialog.getCustomView().findViewById(R.id.enquiry_complete_year);
        interestedCountry = materialDialog.getCustomView().findViewById(R.id.enquiry_apply_country);
        interestedCourse = materialDialog.getCustomView().findViewById(R.id.course_to_apply);
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);

        ArrayAdapter aa = new ArrayAdapter(context, android.R.layout.simple_spinner_item, dates);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        completedYear.setAdapter(aa);
        completedYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = dates.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        enquiryDetails = data.enquiry_details;
        qualification.setText(enquiryDetails.qualification);
        interestedCourse.setText(enquiryDetails.interested_course);
        interestedCountry.setText(enquiryDetails.interested_country);
        summary.setText(enquiryDetails.summary);

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFurtherDetails(materialDialog, id, client_name);
//
            }
        });
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEnquiry(id, client_name);
                materialDialog.dismiss();
            }
        });

    }

    private void getStudentInfo() {
        data = App.db().getObject(FragmentKeys.DATA, Data.class);
        enquiryDetails = data.enquiry_details;
        qualificationTv.setText(enquiryDetails.qualification);
        interestCourseTv.setText(enquiryDetails.interested_course);
        destination.setText(enquiryDetails.interested_country);
        summaryTv.setText(enquiryDetails.summary);
    }

    private void getStudentFurtherDetails(final int id, final String client_name) {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("Complete Your Profile")
                .customView(R.layout.fragment_course_enquiry, true)
                .positiveText("Save Details")
                .negativeText("Close")
                .positiveColor(context.getResources().getColor(R.color.green))
                .negativeColor(context.getResources().getColor(R.color.red))
                .show();

        qualification = materialDialog.getCustomView().findViewById(R.id.enquiry_level_completed);
        completedYear = materialDialog.getCustomView().findViewById(R.id.enquiry_complete_year);
        interestedCountry = materialDialog.getCustomView().findViewById(R.id.enquiry_apply_country);
        interestedCourse = materialDialog.getCustomView().findViewById(R.id.course_to_apply);
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);

        materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFurtherDetails(materialDialog, id, client_name);
            }
        });
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
    }

    private void addFurtherDetails(final MaterialDialog materialDialog, final int id, final String client_name) {
        String studentQualification = qualification.getText().toString();
        String studentInterestedCountry = interestedCountry.getText().toString();
        String studentInterestedCourse = interestedCourse.getText().toString();
        String studentSummary = summary.getText().toString();

        if (TextUtils.isEmpty(studentQualification)) {
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentInterestedCountry)) {
            interestedCountry.setError("Enter your qualification");
            interestedCountry.requestFocus();
        } else if (TextUtils.isEmpty(studentInterestedCourse)) {
            interestedCourse.setError("Enter your qualification");
            interestedCourse.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)) {
            summary.setError("Enter your qualification");
            summary.requestFocus();
        } else {
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetails(studentQualification, studentInterestedCountry, studentInterestedCourse, studentSummary, App.db().getInt(Keys.USER_ID))
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    App.db().putObject(FragmentKeys.DATA, response.body().data);
                                    MDToast mdToast = MDToast.makeText(context, "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                    mdToast.show();
                                    getEnquiry(id, client_name);
                                    materialDialog.dismiss();

                                }
                            } else {
                                try {
                                    Log.d("client", "onResponse: error" + response.errorBody().string());
                                    MDToast mdToast = MDToast.makeText(context, "There was something wrong while saving your info. Please try again!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                                    mdToast.show();
                                } catch (Exception e) {
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Login> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            MDToast mdToast = MDToast.makeText(context, "There is no internet connection. Please try again later!", Toast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                            mdToast.show();
                        }
                    });
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        SearchFragment.clientList.clear();
        if (charText.length() == 0) {
            SearchFragment.clientList.addAll(arrayList);
        } else {
            for (Client wp : arrayList) {
                if (wp.client_name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    SearchFragment.clientList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public class ConsultancyListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.consultancy_logo)
        ImageView consultancyLogo;
        @BindView(R.id.btn_enquiry)
        Button btnEnquiry;
        @BindView(R.id.consultancy_name)
        TextView consultancyName;
        @BindView(R.id.consultancy_whole_layout)
        CardView wholeLayout;

        public ConsultancyListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}