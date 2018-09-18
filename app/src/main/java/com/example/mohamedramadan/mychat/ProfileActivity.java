package com.example.mohamedramadan.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    DatabaseReference reference;
    TextView profile_Name, profile_State;
    CircleImageView image_Profile;
    Intent intent;
    ProgressBar progressBar;
    String CURRENT_STATUT;
    DatabaseReference friendreReference;
    FirebaseAuth firebaseAuth;
    String user_id;
    String reciver_user_id;
    DatabaseReference acceptreference;
    Button send_request_button, declin_request_button;

    DatabaseReference notificationReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        friendreReference = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        friendreReference.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getUid();
        intent = getIntent();
        acceptreference = FirebaseDatabase.getInstance().getReference().child("Friends");
        acceptreference.keepSynced(true);
        send_request_button = findViewById(R.id.send_request);
        declin_request_button = findViewById(R.id.decline_id);
        reciver_user_id = intent.getStringExtra("user_id");
        profile_Name = findViewById(R.id.profile_name);
        progressBar = findViewById(R.id.profile_bar);
        progressBar.setVisibility(View.VISIBLE);
        declin_request_button.setVisibility(View.GONE);
        image_Profile = findViewById(R.id.profileimage);
        profile_State = findViewById(R.id.profile_state);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(reciver_user_id);
        CURRENT_STATUT = "Not_friends";
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);
        acceptreference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(reciver_user_id))
                {
                    CURRENT_STATUT = "Friends";
                    send_request_button.setText("UnFriend");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String state = dataSnapshot.child("user_statue").getValue().toString();
                final String user_image = dataSnapshot.child("user_thumb_image").getValue().toString();
                profile_Name.setText(name);
                profile_State.setText(state);
                Picasso.get().load(user_image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.imageuser1)
                        .into(image_Profile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(user_image)
                                        .placeholder(R.drawable.imageuser1)
                                        .into(image_Profile);
                            }
                        });
                progressBar.setVisibility(View.GONE);
                friendreReference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         if (dataSnapshot.hasChild(reciver_user_id))
                        {
                            String reciver = dataSnapshot.child(reciver_user_id).child("request_type").getValue().toString();
                            if (reciver.equals("send")) {
                                CURRENT_STATUT = "request_send";
                                send_request_button.setText("Cancel Friend Request");

                            }
                            else if(reciver.equals("received"))
                            {
                                CURRENT_STATUT = "request_received";
                                send_request_button.setText("Accept Friend Request");
                                declin_request_button.setVisibility(View.VISIBLE);

                                declin_request_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DeclinFriendRequest();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(!user_id.equals(reciver_user_id)) {
            send_request_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_request_button.setEnabled(false);
                    if (CURRENT_STATUT.equals("Not_friends")) {
                        sendFreindRequestToPerson();
                    }

                    if (CURRENT_STATUT.equals("request_send")) {
                        cancelFreindRequestToPerson("Send Friend Request", "Not_friends");
                    }

                    if (CURRENT_STATUT.equals("request_received")) {
                        acceptFreindRequest();
                    }
                    if (CURRENT_STATUT.equals("Friends")) {
                        unFriend();
                    }
                }
            });
        }
        else
            {
                send_request_button.setVisibility(View.GONE);
                declin_request_button.setVisibility(View.GONE);
            }
    }

    private void DeclinFriendRequest(){
        cancelFreindRequestToPerson("Send Friend Request","Not_friends");
        declin_request_button.setVisibility(View.GONE);
    }

    private void unFriend() {
        acceptreference.child(user_id).child(reciver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                acceptreference.child(reciver_user_id).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            CURRENT_STATUT = "Not_friends";
                            send_request_button.setEnabled(true);
                            send_request_button.setText("Send Friend Request");
                    }
                });
            }
        });
    }

    private void acceptFreindRequest() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        final  String CURRENT_DATA = dateFormat.format(calendar.getTime());

        acceptreference.child(user_id).child(reciver_user_id).child("date").setValue(CURRENT_DATA).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    acceptreference.child(reciver_user_id).child(user_id).child("date").setValue(CURRENT_DATA).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                                cancelFreindRequestToPerson("UnFriend","Friends");
                                declin_request_button.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void cancelFreindRequestToPerson(final String text, final String state) {
        friendreReference.child(user_id).child(reciver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            friendreReference.child(reciver_user_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            send_request_button.setEnabled(true);
                                            send_request_button.setText(text);
                                            CURRENT_STATUT = state;
                                        }
                                }
                            });
                        }
            }
        });
    }

    private void sendFreindRequestToPerson() {
        friendreReference.child(user_id).child(reciver_user_id)
                .child("request_type").setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    friendreReference.child(reciver_user_id).child(user_id)
                            .child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        HashMap<String,String> hashMapdata = new HashMap<>();

                                        hashMapdata.put("From",user_id);
                                        hashMapdata.put("type","request");
                                        notificationReference.child(reciver_user_id).push().setValue(hashMapdata)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    send_request_button.setEnabled(true);
                                                    CURRENT_STATUT = "request_send";
                                                    send_request_button.setText("Cancel Friend Request");
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                }
            }
        });
    }


}
