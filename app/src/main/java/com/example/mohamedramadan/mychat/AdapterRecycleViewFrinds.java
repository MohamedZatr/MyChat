package com.example.mohamedramadan.mychat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class AdapterRecycleViewFrinds extends RecyclerView.ViewHolder {
    View view;
    public AdapterRecycleViewFrinds(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setDate(String state) {
        TextView textView = view.findViewById(R.id.all_user_state);
        textView.setText(state);
    }
    public void setState(String state) {
        TextView textView = view.findViewById(R.id.all_user_state);
        textView.setText(state);
    }
    public void setName(String name)
    {
        TextView textView = view.findViewById(R.id.all_user_name);
        textView.setText(name);
    }
    public void setImage(final String urlImage)
    {
        final ImageView imageView = view.findViewById(R.id.all_user_image);
        Picasso.get().load(urlImage)
                .placeholder(R.drawable.imageuser1).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(urlImage)
                        .placeholder(R.drawable.imageuser1).into(imageView);
            }
        });

    }

    public void setUserOnline(String online)
    {
        ImageView imageView = view.findViewById(R.id.online);
        if (online.equals("true"))
        imageView.setVisibility(View.VISIBLE);
    }

}
