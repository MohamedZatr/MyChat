package com.example.mohamedramadan.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Register extends AppCompatActivity {
private Toolbar toolbar;
private EditText name, email, password, re_password;
private String val_Name, val_Email, val_Password, val_Re_Password;
private Button register;
private final String ERROR = "Please fill this Field";
private TextInputLayout inputLayoutpass1,inputLayoutpass2;
private FirebaseAuth firebaseAuth;
private ProgressBar progressBar;
private DatabaseReference reference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = findViewById(R.id.register_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.name_register);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password1_register);
        re_password = findViewById(R.id.password2_register);
        register = findViewById(R.id.button_register);
        inputLayoutpass1 = findViewById(R.id.password1);
        inputLayoutpass2 = findViewById(R.id.password2);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loading);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputLayoutpass1.setPasswordVisibilityToggleEnabled(true);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        re_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputLayoutpass2.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val_Name = name.getText().toString();
                val_Email = email.getText().toString();
                val_Password = password.getText().toString();
                val_Re_Password = re_password.getText().toString();
                registeration(val_Name,val_Email,val_Password,val_Re_Password);
            }
        });


    }

   private void registeration(final String vname, String vemail, String vpassword, String vrepassword)
    {
        if (vname.trim().isEmpty())
        {
            name.setError(ERROR);
        }
        if(vemail.trim().isEmpty())
        {
            email.setError(ERROR);
        }
        if(vpassword.trim().isEmpty())
        {
            password.setError(ERROR);
            inputLayoutpass1.setPasswordVisibilityToggleEnabled(false);
        }
        if(vrepassword.trim().isEmpty())
        {
            re_password.setError(ERROR);
            inputLayoutpass2.setPasswordVisibilityToggleEnabled(false);
        }
        if(!vname.trim().isEmpty() && !vemail.trim().isEmpty() && !vpassword.trim().isEmpty() && !vrepassword.trim().isEmpty())
        {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(vemail,vpassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String curruntUserId = firebaseAuth.getCurrentUser().getUid();
                                String device_tocken = FirebaseInstanceId.getInstance().getToken();
                                reference = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(curruntUserId);
                                reference.keepSynced(true);
                                reference.child("user_name").setValue(vname);
                                reference.child("Device_token").setValue(device_tocken);
                                reference.child("user_statue").setValue("Hello every one");
                                reference.child("user_image").setValue("default_profile");
                                reference.child("user_thumb_image").setValue("default_image")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Intent intent = new Intent(Register.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Register.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                ;

                            }
                            else {
                                Toast.makeText(Register.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
