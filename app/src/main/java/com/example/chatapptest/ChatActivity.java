package com.example.chatapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.example.chatapptest.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessageAdapter messageAdapter;
    ArrayList<Message> messages;
    FirebaseDatabase database;

    // for handling unique chat room
    String senderRoom;
    String receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String receiverUid = intent.getStringExtra("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();

        // creating unique room for chat
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        messageAdapter = new MessageAdapter(this,messages,senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(messageAdapter);

        database.getReference().child("chats").child(senderRoom)
                .child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Message message = snapshot1.getValue(Message.class);
                    message.setMessageId(snapshot1.getKey());
                    messages.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageTxt = binding.messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt,senderUid,date.getTime());
                binding.messageBox.setText("");
                // send message to both room, sender room and receiver room
                // here below is sending message to sender room
                String randomKey = database.getReference().push().getKey();

                // handles the last msg and show it to the top
                HashMap<String,Object> lastMsgObject = new HashMap<>();
                lastMsgObject.put("lastMsg",message.getMessage());
                lastMsgObject.put("lastTime",date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObject);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObject);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // when sent is success then also update the receiver room
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });

            }
        });

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}