package com.example.mohamedramadan.mychat;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecycleviewofMessage extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    FirebaseAuth firebaseAuth;
    public AdapterRecycleviewofMessage(List<Message> messageList) {
        this.messages = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View sender = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message_layout, parent, false);
        View reciver = LayoutInflater.from(parent.getContext()).inflate(R.layout.reciverer_message_layout, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        if (viewType == 1)
        {
            return new SendereHolder(reciver);
        }
        else
            return new ReciverHolder(sender);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder.getItemViewType() == 1) {
            SendereHolder sendereHolder = (SendereHolder) holder;
            sendereHolder.textView.setText(message.getMessage());
        }
        else {
            ReciverHolder reciverHolder = (ReciverHolder) holder;
            reciverHolder.textView.setText(message.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendereHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CircleImageView circleImageView;

        public SendereHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_message);
        }
    }
    public class ReciverHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CircleImageView circleImageView;

        public ReciverHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser().getUid().toString().equals(messages.get(position).getFrom())) {
            return 1;
        }
        else return 0;
    }
}
