package susankyatech.com.consultancymanagement.Adapters;

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
import android.widget.CheckBox;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.EnquiryAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Fragment.ConsultancyProfileFragment;
import susankyatech.com.consultancymanagement.Fragment.OpenInquirySelectCountryFragment;
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
    private int selectedYear;
    private String selectedLevel;

    List<Integer> dates = new ArrayList<>();
    String[] qualificationList = {"+2", "Bachelors", "Masters"};

    ArrayAdapter dateAdapter, levelAdapter;

    private EditText qualification, summary, userName, userEmail, userAddress, userPhone;
    private CheckBox ieltsCB, toeflCB, greCB, pteCB, satCB;

    private Spinner completedYear, qualificationSpinner;

    public ConsultancyListAdapter(List<Client> clientList, Context context) {
        this.clientList = clientList;
        this.context = context;
        this.arrayList = new ArrayList<Client>();
        this.arrayList.addAll(SearchFragment.clientList);
        this.data = App.db().getObject(FragmentKeys.DATA, Data.class);

        dateAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, dates);
        levelAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, qualificationList);

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        if (clientList.get(i).logo == null){
            Picasso.get().load(R.drawable.banner).into(holder.consultancyLogo);
        }else{
            Picasso.get().load(clientList.get(i).logo).into(holder.consultancyLogo);
        }

        holder.btnEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putInt("client_id", clientList.get(i).id);
                bundle.putString("client_name", clientList.get(i).client_name);

                if (data.enquiry_details == null) {
                    getStudentFurtherDetails(clientList.get(i).id, clientList.get(i).client_name);
                } else {

                    FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                    OpenInquirySelectCountryFragment openInquirySelectCountryFragment = new OpenInquirySelectCountryFragment();
                    openInquirySelectCountryFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.main_container, openInquirySelectCountryFragment).addToBackStack(null).commit();
//                    getEnquiry(clientList.get(i).id, clientList.get(i).client_name);
                }

            }
        });
    }

    private void getStudentFurtherDetails(final int id, final String client_name) {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("Complete your Profile")
                .customView(R.layout.fragment_course_enquiry, true)
                .positiveText("Save Details")
                .negativeText("Close")
                .positiveColor(context.getResources().getColor(R.color.green))
                .negativeColor(context.getResources().getColor(R.color.red))
                .show();

        qualification = materialDialog.getCustomView().findViewById(R.id.enquiry_level_completed);
        completedYear = materialDialog.getCustomView().findViewById(R.id.enquiry_complete_year);
        summary = materialDialog.getCustomView().findViewById(R.id.about_you);
        qualificationSpinner = materialDialog.getCustomView().findViewById(R.id.qualification_spinner);
        userName = materialDialog.getCustomView().findViewById(R.id.enquiry_name);
        userAddress = materialDialog.getCustomView().findViewById(R.id.enquiry_address);
        userEmail = materialDialog.getCustomView().findViewById(R.id.enquiry_email);
        userPhone = materialDialog.getCustomView().findViewById(R.id.enquiry_phone);
        satCB = materialDialog.getCustomView().findViewById(R.id.cv_sat);
        ieltsCB = materialDialog.getCustomView().findViewById(R.id.cv_ielts);
        greCB = materialDialog.getCustomView().findViewById(R.id.cv_gre);
        pteCB = materialDialog.getCustomView().findViewById(R.id.cv_pte);
        toeflCB = materialDialog.getCustomView().findViewById(R.id.cv_tofel);
        ArrayAdapter dateAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, dates);
        ArrayAdapter levelAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, qualificationList);

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        userEmail.setText(data.email);
        userName.setText(data.name);
        userPhone.setText(data.phone);
        userAddress.setText(data.address);


        completedYear.setAdapter(dateAdapter);
        qualificationSpinner.setAdapter(levelAdapter);

        completedYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = dates.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        qualificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLevel = qualificationList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        String studentSummary = summary.getText().toString();
        String testsAttended = getTestsString();

        if (TextUtils.isEmpty(studentQualification)) {
            qualification.setError("Enter your qualification");
            qualification.requestFocus();
        } else if (TextUtils.isEmpty(studentSummary)) {
            summary.setError("Enter Summary");
            summary.requestFocus();
        } else {
            String studentCourseCompleted = selectedLevel + ", " + studentQualification;
            EnquiryAPI enquiryAPI = App.consultancyRetrofit().create(EnquiryAPI.class);
            enquiryAPI.saveDetailsNew(studentCourseCompleted, studentSummary, App.db().getInt(Keys.USER_ID), selectedYear, testsAttended)
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    App.db().putObject(FragmentKeys.DATA, response.body().data);
                                    MDToast mdToast = MDToast.makeText(context, "Your info is successfully saved!", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                    mdToast.show();
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

    private String getTestsString() {
        String tests = "";
        if (toeflCB.isChecked())
            tests += "TOEFL, ";
        if (satCB.isChecked())
            tests += "SAT, ";
        if (greCB.isChecked())
            tests += "GRE, ";
        if (ieltsCB.isChecked())
            tests += "IELTS, ";
        if (pteCB.isChecked())
            tests += "PTE";

        return tests;
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
