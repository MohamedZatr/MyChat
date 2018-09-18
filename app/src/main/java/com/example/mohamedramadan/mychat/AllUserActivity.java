package com.example.mohamedramadan.mychat;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AllUserActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    RecyclerView allUserList;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    int pos = 0;
    FirebaseRecyclerAdapter<Users, AdapterRecyclerViiew> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        allUserList = findViewById(R.id.recycleview);
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.keepSynced(true);
        if (savedInstanceState != null)
        {
           pos = savedInstanceState.getInt("pos");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Query query = reference.orderByChild("user_name").limitToLast(50);
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new FirebaseRecyclerAdapter<Users, AdapterRecyclerViiew>(options) {
            @Override
            protected void onBindViewHolder(AdapterRecyclerViiew holder, final int position, Users model) {
                holder.setUser_statue(model.getUser_statue());
                holder.setUser_name(model.getUser_name());
                holder.setUser_image(model.getUser_thumb_image());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = position;
                        String view_uer_id = getRef(position).getKey();
                        Intent intent = new Intent(AllUserActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id", view_uer_id);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public AdapterRecyclerViiew onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AllUserActivity.this).inflate(R.layout.all_user_layout, parent, false);
                return new AdapterRecyclerViiew(view);
            }
        };
        allUserList.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                allUserList.smoothScrollToPosition(pos);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("pos", pos);
    }

}
