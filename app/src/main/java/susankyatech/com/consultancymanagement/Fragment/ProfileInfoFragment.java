package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import susankyatech.com.consultancymanagement.API.ClientAPI;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Application.MySpannable;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Model.ProfileInfo;
import susankyatech.com.consultancymanagement.Model.Client;
import susankyatech.com.consultancymanagement.Model.Detail;
import susankyatech.com.consultancymanagement.Model.Login;
import susankyatech.com.consultancymanagement.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {

    @BindView(R.id.profile_established_date)
    TextView establishedDate;
    @BindView(R.id.location)
    TextView locationTV;
    @BindView(R.id.phone_number)
    TextView phoneNoTV;
    @BindView(R.id.services)
    TextView servicesTV;
    @BindView(R.id.description)
    TextView descriptionTV;
    @BindView(R.id.progressBarLayout)
    View progressLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressTV)
    TextView progressTextView;
    @BindView(R.id.whole_layout)
    RelativeLayout wholeLayout;
    @BindView(R.id.btn_edit)
    FancyButton editInfo;
    @BindView(R.id.message)
    TextView message;

    public static final int MAX_LINES = 3;

    private Client client;
    private Detail detail;
    private int clientId, detail_id;
    private String clientName;

    private EditText established, location, phoneNo, description;


    public ProfileInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wholeLayout.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        if (getArguments() != null) {
            clientId = getArguments().getInt("clientId", 0);
            clientName = getArguments().getString("clientName");
        }
        Log.d("countryAdd", clientId + "");

        if (clientId == 0) {
            getProfileInfo();

        } else {
            editInfo.setVisibility(View.GONE);
            getClientProfileInfo();
        }


        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                        .title("Edit Your Info")
                        .customView(R.layout.edit_client_info, true)
                        .positiveText("Save Details")
                        .negativeText("Close")
                        .positiveColor(getResources().getColor(R.color.green))
                        .negativeColor(getResources().getColor(R.color.red))
                        .show();

                established = materialDialog.getCustomView().findViewById(R.id.established_date);
                location = materialDialog.getCustomView().findViewById(R.id.location);
                phoneNo = materialDialog.getCustomView().findViewById(R.id.phone_number);
                description = materialDialog.getCustomView().findViewById(R.id.description);

                established.setText(detail.established);
                location.setText(detail.location);
                phoneNo.setText(detail.phone);
//                emailId.setText(client);
                description.setText(detail.description);

                materialDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editDetails(materialDialog);
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

    private void editDetails(MaterialDialog materialDialog) {
        String clientEstablished = established.getText().toString();
        String clientLocation = location.getText().toString();
        String clientPhone = phoneNo.getText().toString();
        String clientAchievements = description.getText().toString();

        if (TextUtils.isEmpty(clientEstablished)) {
            established.setError("Enter Established Date");
            established.requestFocus();
        } else if (TextUtils.isEmpty(clientLocation)) {
            location.setError("Enter Location");
            location.requestFocus();
        } else if (TextUtils.isEmpty(clientPhone)) {
            phoneNo.setError("Enter Phone Number");
            phoneNo.requestFocus();
        } else if (clientPhone.length() < 10) {
            phoneNo.setError("Enter Valid Phone Number");
            phoneNo.requestFocus();
        } else if (clientPhone.length() > 10) {
            phoneNo.setError("Enter Valid Phone Number");
            phoneNo.requestFocus();
        } else if (TextUtils.isEmpty(clientAchievements)) {
            description.setError("Enter Achievements");
            description.requestFocus();
        } else {
            saveDetails(clientEstablished, clientLocation, clientPhone, clientAchievements);
            materialDialog.dismiss();
        }

    }

    private void saveDetails(String clientEstablished, String clientLocation, String clientPhone, String clientDescription) {
        ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        Client client1 = App.db().getObject(FragmentKeys.CLIENT, Client.class);
        detail_id = client1.detail.id;
        Log.d("asd", "saveDetails: " + detail_id);
        ProfileInfo clientDetail = new ProfileInfo();
        clientDetail.detail_id = detail_id;
        clientDetail.achievements = clientDescription;
        clientDetail.phone = clientPhone;
        clientDetail.location = clientLocation;
        clientDetail.established = clientEstablished;

        Log.d("asd", "saveDetails: " + clientDetail.detail_id + clientDetail.achievements + clientDetail.phone + clientDetail.location + clientDetail.established);

        clientAPI.addClient(clientDetail).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        progressLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        wholeLayout.setVisibility(View.GONE);
                        Log.d("asd", "onResponse: " + response.body().data.address + response.body().message);
                        MDToast mdToast = MDToast.makeText(getActivity(), "" + response.body().message, Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                        mdToast.show();
                        getProfileInfo();
                    }
                } else {
                    try {
                        Log.d("asd", response.errorBody().string());
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
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
                Log.d("client", "onFailure:tala " + t);
            }
        });
    }

    private void getClientProfileInfo() {
        final ClientAPI clientAPI = App.consultancyRetrofit().create(ClientAPI.class);
        Log.d("OOPS", clientId + "");
        clientAPI.getSingleClient(ConsultancyProfileFragment.clientStaticID).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        wholeLayout.setVisibility(View.VISIBLE);

                        client = response.body().data.client;
                        detail = response.body().data.client.detail;

                        if (detail.achievements == null) {
                            descriptionTV.setText("");
                        } else {
                            String html = detail.achievements;
                            html = html.replaceAll("<!--.*?-->", "");
                            descriptionTV.setText(Html.fromHtml(html));
                        }
                        String services = "";
                        Log.d("asd", "onResponse: " + client.subjects);

                        for (int i = 0; i < client.subjects.size(); i++) {
                            if (i == client.subjects.size() - 1) {
                                services += client.subjects.get(i).name;
                            } else {
                                services += client.subjects.get(i).name + ", ";
                            }
                        }
                        servicesTV.setText(services);
                        establishedDate.setText(detail.established);
                        phoneNoTV.setText(detail.phone);

                        int lineCount = descriptionTV.getLineCount();
                        Log.d("poi", "onResponse: " + lineCount);
                        makeTextViewResizable(descriptionTV, lineCount, "View More", true);
                    }
                } else {
                    try {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        Log.d("coverPic", response.errorBody().string());

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                Log.d("client", "onFailure:tala " + t);
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
                        wholeLayout.setVisibility(View.VISIBLE);
                        editInfo.setVisibility(View.VISIBLE);
                        client = response.body().data.client;
                        detail = response.body().data.client.detail;
                        establishedDate.setText(detail.established);
                        phoneNoTV.setText(detail.phone);
                        locationTV.setText(detail.location);
                        if (detail.achievements != null) {
                            descriptionTV.setText(detail.achievements);
                        }
                        int lineCount = descriptionTV.getLineCount();
                        Log.d("poi", "onResponse: " + lineCount);
                        makeTextViewResizable(descriptionTV, lineCount, "View More", false);

                    }
                } else {
                    try {
                        progressLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        Log.d("client", "onResponse: error" + response.errorBody().string());
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                Log.d("client", "onFailure:tala " + t);
            }
        });
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                Log.d("poi", "onGlobalLayout: " + maxLine);
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().toString();
                } else if (maxLine > 0 && maxLine >= 3) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().toString();
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {

            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    Log.d("spannable", "onClick: " + tv.getTag().toString());
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        makeTextViewResizable(tv, maxLine, "View More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

}
