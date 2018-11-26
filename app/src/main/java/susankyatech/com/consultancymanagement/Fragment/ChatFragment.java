package susankyatech.com.consultancymanagement.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Activity.MainActivity;
import susankyatech.com.consultancymanagement.Adapters.MessageAdapter;
import susankyatech.com.consultancymanagement.Application.App;
import susankyatech.com.consultancymanagement.Generic.FragmentKeys;
import susankyatech.com.consultancymanagement.Model.Data;
import susankyatech.com.consultancymanagement.Model.Message;
import susankyatech.com.consultancymanagement.R;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    @BindView(R.id.message_list)
    RecyclerView recyclerView;
    @BindView(R.id.input_message)
    EditText inputMessage;
    @BindView(R.id.send_message_btn)
    ImageView sendMessage;

    private MessageAdapter adapter;
    private List<Message> messagesList;

    private int receiverId, senderId;

    Data data;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Send Message");
        init();
        return view;
    }

    private void init() {
        if (getArguments() != null){
            receiverId = getArguments().getInt("client_id");
        }
        data = App.db().getObject(FragmentKeys.DATA, Data.class);

        senderId = data.id;

        getMessageList();

    }

    private void getMessageList() {
        messagesList = new ArrayList<>();
        messagesList.add(new Message(senderId,receiverId, "hi baby....sanchai chau"));
        messagesList.add(new Message(receiverId,senderId, "hey....ahh sanchai...timi"));
        messagesList.add(new Message(senderId,receiverId, "ahh ma ni sanchai chu ni"));
        adapter = new MessageAdapter(messagesList, senderId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

}
