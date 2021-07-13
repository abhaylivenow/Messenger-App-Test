package com.example.chatapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatapptest.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    UserAdapter userAdapter;
    ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init Firebase Database and recycler view
        database = FirebaseDatabase.getInstance();
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this,userList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(userAdapter);

        // go to the database, create object of user from database, and then populate it to Recycler view
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){

                    User currentUser = postSnapshot.getValue(User.class);
                    userList.add(currentUser);

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.group){

        }else if(item.getItemId() == R.id.invite){

        }else if(item.getItemId() == R.id.search){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}