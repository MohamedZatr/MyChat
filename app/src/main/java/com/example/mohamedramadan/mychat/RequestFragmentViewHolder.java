package com.example.mohamedramadan.mychat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragmentViewHolder extends RecyclerView.ViewHolder {
    public View view;
    private CircleImageView userImage;
    Button acceptButton, declineButton;
    private TextView userNameTextView, userStateTextView;

    public RequestFragmentViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        this.userImage = view.findViewById(R.id.user_image_request);
        this.acceptButton = view.findViewById(R.id.accept_request);
        this.declineButton = view.findViewById(R.id.decline_request);
        this.userNameTextView = view.findViewById(R.id.user_name_request);
        this.userStateTextView = view.findViewById(R.id.user_state_request);
    }

    public void setUserImage(final String urlImage) {
        Picasso.get().load(urlImage).placeholder(R.drawable.imageuser1)
                .into(userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(urlImage).placeholder(R.drawable.imageuser1)
                                .into(userImage);
                    }
                });
    }

    public void setUserName(String name) {
        userNameTextView.setText(name);
    }

    public void setUserState(String state) {
        userStateTextView.setText(state);
    }
}
