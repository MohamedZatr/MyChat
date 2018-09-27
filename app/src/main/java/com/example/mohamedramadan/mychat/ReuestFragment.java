package com.example.mohamedramadan.mychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReuestFragment extends Fragment {
    private View view;
    private RecyclerView requestlist;
    private DatabaseReference friendRequestReference,usersReference,friendReference;
    private FirebaseAuth firebaseAuth;
    private String cuurnt_user_id;
    public ReuestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reuest, container, false);

        requestlist = view.findViewById(R.id.request_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        requestlist.setLayoutManager(layoutManager);

        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        firebaseAuth = FirebaseAuth.getInstance();
        cuurnt_user_id = firebaseAuth.getCurrentUser().getUid();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = friendRequestReference.child(cuurnt_user_id).orderByChild("request_type").equalTo("received").limitToLast(50);
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setLifecycleOwner(getActivity())
                .setQuery(query,Request.class)
                .build();

        FirebaseRecyclerAdapter<Request,RequestFragmentViewHolder> adapter = new FirebaseRecyclerAdapter<Request, RequestFragmentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final RequestFragmentViewHolder holder, int position, Request model) {
                final String user_id = getRef(position).getKey();
                usersReference.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("user_name").getValue().toString();
                        String userState = dataSnapshot.child("user_statue").getValue().toString() ;
                        String userImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                        holder.setUserName(userName);
                        holder.setUserState(userState);
                        holder.setUserImage(userImage);
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(),ProfileActivity.class);
                                intent.putExtra("user_id",user_id);
                                startActivity(intent);
                            }
                        });

                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
                                final  String CURRENT_DATA = dateFormat.format(calendar.getTime());

                                friendReference.child(cuurnt_user_id).child(user_id).child("date").setValue(CURRENT_DATA).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            friendReference.child(cuurnt_user_id).child(user_id).child("lasttime").setValue(ServerValue.TIMESTAMP);
                                            friendReference.child(user_id).child(cuurnt_user_id).child("date").setValue(CURRENT_DATA).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    friendReference.child(user_id).child(cuurnt_user_id).child("lasttime").setValue(ServerValue.TIMESTAMP);
                                                    declinerequest(user_id);

                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        });

                        holder.declineButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    declinerequest(user_id);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public RequestFragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.friend_request_layout,parent,false);
                return new RequestFragmentViewHolder(view);
            }
        };
    requestlist.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    }
 private void declinerequest(final String reciver_id)
 {
     friendRequestReference.child(cuurnt_user_id).child(reciver_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
             if (task.isSuccessful())
             {
                 friendRequestReference.child(reciver_id).child(cuurnt_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful())
                         {

                         }
                     }
                 });
             }
         }
     });
 }
}
