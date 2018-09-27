package com.example.mohamedramadan.mychat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecyclerViiew extends RecyclerView.ViewHolder {
    View view;
    public AdapterRecyclerViiew(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setUser_name(String user_name)
    {
        TextView name = view.findViewById(R.id.all_user_name);
        name.setText(user_name);
    }
    public void setUser_image(String user_image)
    {
        CircleImageView imageView = view.findViewById(R.id.all_user_image);
        Picasso.get().load(user_image).placeholder(R.drawable.imageuser1).into(imageView);
    }

    public void setUser_statue(String user_statue)
    {
        TextView state = view.findViewById(R.id.all_user_state);
        state.setText(user_statue);
    }
    public void setUser_thumb_image(final String user_thumb_image)
    {
        final CircleImageView imageView = view.findViewById(R.id.all_user_image);
        Picasso.get().load(user_thumb_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.imageuser1)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(user_thumb_image).placeholder(R.drawable.imageuser1).into(imageView);
                    }
                });

    }
}
