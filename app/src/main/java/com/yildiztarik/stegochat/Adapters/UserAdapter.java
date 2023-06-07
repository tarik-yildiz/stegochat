package com.yildiztarik.stegochat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.Activities.ShowOtherUserActivity;
import com.yildiztarik.stegochat.Utils.ActivityManager;
import com.yildiztarik.stegochat.Utils.Constants;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<String> userKeyList;
    Activity activity;
    Context context;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;




    public UserAdapter(List<String> userKeyList, Activity activity, Context context) {
        this.userKeyList = userKeyList;
        this.activity = activity;
        this.context = context;

        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();

    }





    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout,viewGroup,false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        reference.child("Users").child(userKeyList.get(position).toString()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String username= snapshot.getValue().toString();
                viewHolder.username.setText(username);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewHolder.linearLayoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance(context).startActivityWithMessage(ShowOtherUserActivity.class,Constants.USER_ID,userKeyList.get(position).toString());
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return userKeyList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        LinearLayout linearLayoutUser;
        public ViewHolder(View view) {
            super(view);
            username=view.findViewById(R.id.textViewListItemUserName);
            linearLayoutUser=view.findViewById(R.id.linearLayoutUserInfo);

        }
        public void setOnClick(){

        }
    }
}