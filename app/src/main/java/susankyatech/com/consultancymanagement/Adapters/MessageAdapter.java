package susankyatech.com.consultancymanagement.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import susankyatech.com.consultancymanagement.Model.Message;
import susankyatech.com.consultancymanagement.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messagesList;
    private int userId;

    public MessageAdapter(List<Message> messagesList, int userId) {
        this.messagesList = messagesList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message_layout, viewGroup, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        final Message messages = messagesList.get(i);

        int fromUserID = messages.sender_id;

        messageViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);

        if (fromUserID ==userId ){
            messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
            messageViewHolder.senderMessageText.setText(messages.message);
        } else {
            messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);

            messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

            messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.reciever_message_layout);
            messageViewHolder.receiverMessageText.setText(messages.message);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sender_message_text)
        TextView senderMessageText;
        @BindView(R.id.receiver_message_text)
        TextView receiverMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
