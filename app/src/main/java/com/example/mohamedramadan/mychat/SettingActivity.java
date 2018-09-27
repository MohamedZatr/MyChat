package com.example.mohamedramadan.mychat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private CircleImageView circleImageView;
    private TextView userName, userState;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private String current_user;
    private ProgressBar progressBar;
    FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri resultUri;
    Bitmap bitmap = null;
    StorageReference imagerefrence;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        circleImageView = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        userState = findViewById(R.id.user_state);
        firebaseAuth = FirebaseAuth.getInstance();
        imagerefrence = FirebaseStorage.getInstance().getReference().child("thumb_image");
        firebaseStorage  = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        current_user = firebaseAuth.getCurrentUser().getUid();
        progressBar = findViewById(R.id.pro_of_image);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String state = dataSnapshot.child("user_statue").getValue().toString();
                String user_image = dataSnapshot.child("user_image").getValue().toString();
                final String user_thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();
                userName.setText(name);
                userState.setText(state);
                if (user_image.contains("google")){
                    progressBar.setVisibility(View.VISIBLE);
                    Picasso.get().load(user_thumb_image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.imageuser1)
                            .into(circleImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(user_thumb_image).placeholder(R.drawable.imageuser1).into(circleImageView);
                                }
                            });

                }
                else circleImageView.setImageDrawable(getDrawable(R.drawable.imageuser1));
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void editUserImage(View view) {
        Intent gallryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        gallryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(gallryIntent, "Select your image"), REQUEST_CODE);
    }

    public void editUserName(View view) {
        dialog("Name");
    }

    public void editUserState(View view) {
        dialog("State");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                 resultUri = result.getUri();
                 File thumb_file = new File(resultUri.getPath());


                 String fileExtention  = resultUri.toString().substring(resultUri.toString().lastIndexOf("."));

                try {
                    bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream  byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final  byte[] bytes = byteArrayOutputStream.toByteArray();


                StorageReference filePath = storageReference.child(firebaseAuth.getCurrentUser().getUid()+fileExtention);
                final StorageReference thumb_file_path = imagerefrence.child(firebaseAuth.getCurrentUser().getUid()+".jpg");
                 filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if(task.isSuccessful())
                         {
                             final String imageurl = task.getResult().getDownloadUrl().toString();

                             UploadTask uploadTask = thumb_file_path.putBytes(bytes);
                             uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                 @Override
                                 public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        if (thumb_task.isSuccessful())
                                        {
                                            String  downloadurl = thumb_task.getResult().getDownloadUrl().toString();
                                            Map update_data = new HashMap();
                                            update_data.put("user_image",imageurl);
                                            update_data.put("user_thumb_image",downloadurl);

                                            reference.updateChildren(update_data)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(SettingActivity.this, "Your Image is Uploaded", Toast.LENGTH_SHORT).show();
                                                            circleImageView.setImageURI(resultUri);
                                                        }
                                                    });
                                        }
                                 }
                             });


                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                         progressBar.setVisibility(View.GONE);
                     }
                 });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void dialog(final String b)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_activity,null);
        builder.setView(view);
        EditText editText = view.findViewById(R.id.edit_text);
        if(b.equals("Name"))
        {
            editText.setText(userName.getText().toString());
        }
        else {
            editText.setText(userState.getText().toString());

        }
        final EditText editText1 = editText;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText1.getText().toString().trim().isEmpty())
                {
                   Toast.makeText(SettingActivity.this, "Please Write " + b , Toast.LENGTH_SHORT).show();
                   editText1.setText(userName.getText().toString());
                }
                else {
                    if(b.equals("Name"))
                    {
                        reference.child("user_name").setValue(editText1.getText().toString());
                        editText1.setText(userState.getText().toString());
                    }
                    else {
                        reference.child("user_statue").setValue(editText1.getText().toString());
                    }
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
            }
        });
        builder.setTitle("Enter Your " + b);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

