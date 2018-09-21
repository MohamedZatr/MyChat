package com.example.mohamedramadan.mychat;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.nio.file.Path;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String user_reciver_id, user_reciver_name;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView username, lastseen;
    private CircleImageView circleImageuser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        reference = FirebaseDatabase.getInstance().getReference();
        user_reciver_id = intent.getStringExtra("user_id");
        user_reciver_name = intent.getStringExtra("user_name");
        toolbar = findViewById(R.id.chat_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View view = LayoutInflater.from(this).inflate(R.layout.chat_custom_bar, null);
        username = view.findViewById(R.id.chat_user_name);
        lastseen = view.findViewById(R.id.chat_last_seen);
        circleImageuser = view.findViewById(R.id.chat_image_user);
        username.setText(user_reciver_name);
        reference.child("Users").child(user_reciver_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String user_reciver_image = dataSnapshot.child("user_thumb_image").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();
                Picasso.get()
                        .load(user_reciver_image)
                        .into(circleImageuser, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(user_reciver_image)
                                        .placeholder(R.drawable.imageuser1).
                                        into(circleImageuser);
                            }
                        });
                if (online.equals("true"))
                {
                    lastseen.setText("Online");
                }
                else
                {
                    long last_seen = Long.parseLong(online);
                    String last_time = LastSeen.getTimeApp(last_seen);
                    lastseen.setText(last_time);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        actionBar.setCustomView(view);
    }
}
