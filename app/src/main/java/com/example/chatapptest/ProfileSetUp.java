package com.example.chatapptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSetUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // you must also ask for permission of accessing the file
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });

        binding.btnSetupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.nameBox.getText().toString();
                if(username.isEmpty()){
                    binding.nameBox.setError("Please enter name");
                    return;
                }

                if(selectedImage != null){
                    StorageReference reference = storage.getReference().child("profile_pics").child(mAuth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String profilePicUrl = uri.toString();
                                        String uid = mAuth.getUid();
                                        String name = binding.nameBox.getText().toString();
                                        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

                                        User user = new User(uid,name,phoneNumber,profilePicUrl);

                                        database.getReference().child("users").setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
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