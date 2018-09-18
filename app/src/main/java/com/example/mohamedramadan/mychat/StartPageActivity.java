package com.example.mohamedramadan.mychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }

    public void register(View view) {
        startActivity(new Intent(StartPageActivity.this,Register.class));
    }
    public void login(View view) {
        startActivity(new Intent(StartPageActivity.this,Login.class));
    }
}
