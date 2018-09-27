package com.example.mohamedramadan.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    FragmentManager fragmentManager;
    MyAdapter myAdapter;
    TabLayout.Tab tab;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currntUser = firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_page_tool_bar);
        setSupportActionBar(toolbar);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        getSupportActionBar().setTitle("My Chat");
        viewPager = findViewById(R.id.main_view_pager);
        tabLayout = findViewById(R.id.main_tab_layout);
        fragmentManager = getSupportFragmentManager();
        myAdapter = new MyAdapter(fragmentManager);
        viewPager.setAdapter(myAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for(int i =0; i< tabLayout.getTabCount();i++)
        {
            tab = tabLayout.getTabAt(i);
            setTabicon(i);
        }

        if (currntUser == null) {
            logout();
        }
        else if(currntUser != null)
        {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (currntUser != null)
                    reference.child(currntUser.getUid().toString()).child("online").onDisconnect().setValue("true");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    void logout() {
        if (currntUser != null) {
            reference.child(currntUser.getUid().toString()).child("online").setValue(ServerValue.TIMESTAMP);
        }
        Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_after_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
               Intent intent = new Intent(MainActivity.this,SettingActivity.class);
               startActivity(intent);
                return true;
            case R.id.logout:
                if (currntUser != null) {
                    reference.child(currntUser.getUid().toString()).child("online").setValue(ServerValue.TIMESTAMP);
                }
                FirebaseAuth.getInstance().signOut();
                logout();
                return true;
            case R.id.all_user:
                startActivity(new Intent(this,AllUserActivity.class));
                return true;
            default:
                super.onOptionsItemSelected(item);
                return true;

        }
    }

    class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new ReuestFragment();
                return fragment;
            }
            if (position == 1) {
                fragment = new ChatFragment();
                return fragment;

            }
            if (position == 2) {
                fragment = new FriendsFragment();
                return fragment;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Request";
                case 1:
                    return "Chats";
                case 2:
                    return "Friends";
                default:
                    return null;
            }
        }
    }

    void setTabicon(int pos) {
        switch (pos) {
            case 0:
                tab.setIcon(R.drawable.select_request);
                break;
            case 1:
                tab.setIcon(R.drawable.select_chat);
                break;
            case 2:
                tab.setIcon(R.drawable.select_friend);
                break;
            default:
                break;
        }
    }
}
