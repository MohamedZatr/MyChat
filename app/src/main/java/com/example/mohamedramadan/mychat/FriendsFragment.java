package com.example.mohamedramadan.mychat;


import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference friends_Reference;
    private FirebaseAuth firebaseAuth;
    String online_user_id;
    DatabaseReference reference;
    private View view;
    FirebaseRecyclerAdapter<Friends,AdapterRecycleViewFrinds> recyclerAdapter;
    FirebaseRecyclerOptions<Friends> firebaseRecyclerOptions;
    private int pos = 0;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friinds, container, false);
        recyclerView = view.findViewById(R.id.list_of_myfrind);
        firebaseAuth = FirebaseAuth.getInstance();
        online_user_id = firebaseAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.keepSynced(true);
        friends_Reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        reference.keepSynced(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (savedInstanceState != null)
        {
            pos = savedInstanceState.getInt("pos");
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Query query = friends_Reference.limitToLast(50);
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(query, Friends.class)
                .setLifecycleOwner(getActivity())
                .build();
        recyclerAdapter = new FirebaseRecyclerAdapter<Friends, AdapterRecycleViewFrinds>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(final AdapterRecycleViewFrinds holder, final int position, Friends model) {
                    holder.setDate(model.getDate());
                    final String list_user_id = getRef(position).getKey();
                    reference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String user_name = dataSnapshot.child("user_name").getValue().toString();
                            String user_image = dataSnapshot.child("user_thumb_image").getValue().toString();
                            holder.setName(user_name);
                            holder.setImage(user_image);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pos = position;
                            Intent intent = new Intent(getContext(),ProfileActivity.class);
                            intent.putExtra("user_id",list_user_id);
                            startActivity(intent);
                        }
                    });

            }

            @Override
            public AdapterRecycleViewFrinds onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.all_user_layout,parent,false);

                return new AdapterRecycleViewFrinds(view);
            }
        };
        recyclerView.setAdapter(recyclerAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("pos",pos);
    }
}
