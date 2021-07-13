package com.example.chatapptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapptest.databinding.RowConversationBinding;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    Context context;
    ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User currentUser = users.get(position);

        holder.binding.username.setText(currentUser.getName());
        Glide.with(context).load(currentUser.getProfileImage()).placeholder(R.drawable.avatar).into(holder.binding.profile);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        RowConversationBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
