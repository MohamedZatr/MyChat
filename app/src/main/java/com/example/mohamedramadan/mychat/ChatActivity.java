package com.example.mohamedramadan.mychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String user_reciver_id, user_reciver_name;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView username, lastseen;
    private CircleImageView circleImageuser;
    private DatabaseReference reference, messageReference,friendReference;
    private ImageButton sendMessage, sendImage;
    private MultiAutoCompleteTextView writeMessage;
    private FirebaseAuth firebaseAuth ;
    String message_sender_id;
    private  List<Message> messages = new ArrayList<>();
    AdapterRecycleviewofMessage adapter;
    private RecyclerView user_message_list;
    LinearLayoutManager linearLayoutManager;
    boolean firstopened = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        message_sender_id =  firebaseAuth.getCurrentUser().getUid().toString();
        user_reciver_id = intent.getStringExtra("user_id");
        user_reciver_name = intent.getStringExtra("user_name");
        friendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        sendMessage = findViewById(R.id.send_message);
        sendImage = findViewById(R.id.select_image_send);
        writeMessage = findViewById(R.id.write_message);

        toolbar = findViewById(R.id.chat_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View view = LayoutInflater.from(this).inflate(R.layout.chat_custom_bar, null);
        username = view.findViewById(R.id.chat_user_name);
        lastseen = view.findViewById(R.id.chat_last_seen);
        messageReference = FirebaseDatabase.getInstance().getReference();
        circleImageuser = view.findViewById(R.id.chat_image_user);
        user_message_list = findViewById(R.id.message_list);
        linearLayoutManager = new LinearLayoutManager(this);
        user_message_list.setLayoutManager(linearLayoutManager);
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

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToUser();
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(writeMessage.getWindowToken(),0);
            }
        });

        fetchMessage();
        adapter = new AdapterRecycleviewofMessage(messages);
        user_message_list.setAdapter(adapter);

    }

    private void sendMessageToUser() {
        final String messagetext = writeMessage.getText().toString();
        if (messagetext.trim().isEmpty())
        {
            Toast.makeText(ChatActivity.this, "Please Write Message", Toast.LENGTH_SHORT).show();
        }
        else {
            Message message = new Message();
            message.setMessage(messagetext);
            message.setSeen(false);
            message.setType("text");

            final Map messageBody = new HashMap();
            messageBody.put("message",message.getMessage());
            messageBody.put("seen",message.isSeen());
            messageBody.put("time",ServerValue.TIMESTAMP);
            messageBody.put("type",message.getType());
            messageBody.put("from",message_sender_id);
            DatabaseReference user_message_key = reference.child("Message")
                    .child(message_sender_id).child(user_reciver_id).push();
            final String user_message_id = user_message_key.getKey();
            reference.child("Message").child(message_sender_id)
                            .child(user_reciver_id)
                    .child(user_message_id)
                    .setValue(messageBody).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    reference.child("Message").child(user_reciver_id)
                            .child(message_sender_id)
                            .child(user_message_id)
                            .setValue(messageBody);
                    writeMessage.setText("");
                }
            });

        }


    }
    private void fetchMessage() {

        messageReference.child("Message").child(message_sender_id).child(user_reciver_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Message message = dataSnapshot.getValue(Message.class);
                        messages.add(message);
                        adapter.notifyDataSetChanged();
                        friendReference.child(message_sender_id).child(user_reciver_id).child("lasttime").setValue(message.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendReference.child(user_reciver_id).child(message_sender_id).child("lasttime").setValue(message.getTime());
                            }
                        });


                        /*if (linearLayoutManager.findLastVisibleItemPosition()!=-1)
                        {
                            user_message_list.scrollToPosition(messages.size()-1);
                        }*/


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


}
