package com.example.mohamedramadan.mychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference UserReference, FriendReference,messageRefrenc;
    private String user_online_id;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        firebaseAuth = FirebaseAuth.getInstance();
        user_online_id = firebaseAuth.getCurrentUser().getUid();
        UserReference = FirebaseDatabase.getInstance().getReference();
        FriendReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_online_id);
        UserReference.keepSynced(true);
        FriendReference.keepSynced(true);
        messageRefrenc = FirebaseDatabase.getInstance().getReference().child("Message");
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayout);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Query query = FriendReference.orderByChild("lasttime").limitToLast(50);
        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setLifecycleOwner(getActivity())
                .setQuery(query, Chat.class)
                .build();
        FirebaseRecyclerAdapter<Chat, AdapterRecycleViewFrinds> adapter = new FirebaseRecyclerAdapter<Chat, AdapterRecycleViewFrinds>(options) {
            @Override
            protected void onBindViewHolder(final AdapterRecycleViewFrinds holder, int position, Chat model) {
                final String user_id = getRef(position).getKey();
                UserReference.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String user_name = dataSnapshot.child("user_name").getValue().toString();
                        //String user_state = dataSnapshot.child("user_statue").getValue().toString();
                        String user_image_url = dataSnapshot.child("user_thumb_image").getValue().toString();
                        String user_online = dataSnapshot.child("online").getValue().toString();
                        holder.setName(user_name);
                        holder.setImage(user_image_url);
                        holder.setUserOnline(user_online);
                        ///////////////////////////////////////////////
                        Query query1 = messageRefrenc.child(user_online_id).child(user_id).orderByKey().limitToLast(1);
                        query1.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if (dataSnapshot.exists()) {
                                    String lastmessage = dataSnapshot.child("message").getValue().toString();
                                    holder.setState(lastmessage);
                                }
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
                        /////////////////////////////////////////////
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dataSnapshot.child("online").exists()) {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("user_id", user_id);
                                    intent.putExtra("user_name", user_name);
                                    startActivity(intent);
                                } else {
                                    UserReference.child("Users").child(user_id).child("online")
                                            .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Intent intent = new Intent(getContext(), ChatActivity.class);
                                            intent.putExtra("user_id", user_id);
                                            intent.putExtra("user_name", user_name);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public AdapterRecycleViewFrinds onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.all_user_layout, parent, false);
                return new AdapterRecycleViewFrinds(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

}
