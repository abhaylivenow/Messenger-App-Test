package com.example.chatapptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.chatapptest.databinding.ActivityProfileSetUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileSetUp extends AppCompatActivity {

    ActivityProfileSetUpBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSetUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCancelable(false);

        // get the reference of all firebase stuffs
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // open gallery when this button is clicked
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // you must also ask for permission of accessing the file
                // askUserPermission();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });

        // handle when user click final setup profile button
        // here all the uploading to the database is done
        binding.btnSetupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String username = binding.nameBox.getText().toString();
                // handle validation
                if(username.isEmpty()){
                    binding.nameBox.setError("Please enter name");
                    return;
                }

                // selecting image is not necessary
                // handle appropriately when image is selected
                if(selectedImage != null){
                    // get the reference to the Firebase storage and upload, name of the file will be the uid of that user
                    StorageReference reference = storage.getReference().child("profile_pics").child(mAuth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            // when task is successful, then also get the download link of the profile image just uploaded
                            if(task.isSuccessful()){
                                // get download url
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String profilePicUrl = uri.toString();
                                        String uid = mAuth.getUid();
                                        String name = binding.nameBox.getText().toString();
                                        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

                                        // get all data and create a user model from that data
                                        User user = new User(uid,name,phoneNumber,profilePicUrl);

                                        // add this user to the Firebase database
                                        database.getReference().child("users").child(uid).setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        // when everything goes well jump to the main chat activity
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(ProfileSetUp.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }else {
                    String uid = mAuth.getUid();
                    String name = binding.nameBox.getText().toString();
                    String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

                    // get all data and create a user model from that data
                    User user = new User(uid,name,phoneNumber,"No Image");

                    // add this user to the Firebase database
                    database.getReference().child("users").child(uid).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // when everything goes well jump to the main chat activity
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(ProfileSetUp.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(data.getData() != null){
                binding.imageView.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}