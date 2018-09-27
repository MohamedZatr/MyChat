package com.example.mohamedramadan.mychat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class Offline extends Application {
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentuser;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //load Picture offline Picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser();
        String online_user_id = firebaseAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(online_user_id);
        reference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

        /*if (currentuser != null)
        {
            String online_user_id = firebaseAuth.getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(online_user_id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s = ServerValue.TIMESTAMP.toString();

                    currentuser = null;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }*/

    }
}
