package com.yildiztarik.stegochat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.Adapters.FriendReqAdapter;
import com.yildiztarik.stegochat.Utils.ActivityManager;
import com.yildiztarik.stegochat.Utils.Constants;
import com.yildiztarik.stegochat.Utils.UsernameValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowMyProfileActivity extends AppCompatActivity {
    TextView textViewShowProfileUsername, textViewShowingUsers2;

    EditText editTextNewUsername;

    ConstraintLayout constraintLayoutSetUsername, constraintLayoutShowProfileShowFriends,
            constraintLayoutShowProfileShowRequests, constraintLayoutShowProfileLogout,
            constraintLayoutShowProfileBack;

    RecyclerView recyclerViewShowProfileFriends;

    List<String> friends, userFriendsAndRequests;
    FriendReqAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference referenceFriends,referenceClean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_profile);
        initComponents();
        setOnClickListeners();
        setRecyclerViewUsers();
        setUserNameText();
    }

    private void initComponents() {
        friends = new ArrayList<>();
        userFriendsAndRequests = new ArrayList<>();

        textViewShowProfileUsername = findViewById(R.id.textViewShowProfileUsername2);
        textViewShowingUsers2 = findViewById(R.id.textViewShowingUsers2);
        editTextNewUsername=findViewById(R.id.editTextNewUsername);

        constraintLayoutSetUsername = findViewById(R.id.constraintLayoutSetUsername);
        constraintLayoutShowProfileShowFriends = findViewById(R.id.constraintLayoutShowProfileShowFriends);
        constraintLayoutShowProfileShowRequests = findViewById(R.id.constraintLayoutShowProfileShowRequests);
        constraintLayoutShowProfileLogout = findViewById(R.id.constraintLayoutShowProfileLogout);
        constraintLayoutShowProfileBack = findViewById(R.id.constraintLayoutShowProfileBack);

        recyclerViewShowProfileFriends = findViewById(R.id.recyclerViewShowProfileFriends);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        referenceFriends = firebaseDatabase.getReference().child(Constants.CHILD_FRIEND_REQUEST);

    }

    private void setUserNameText()
    {
        firebaseDatabase.getReference().child("Users").child(user.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textViewShowProfileUsername.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setRecyclerViewUsers() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewShowProfileFriends.setLayoutManager(layoutManager);
        adapter = new FriendReqAdapter(userFriendsAndRequests, this, getApplicationContext());
        recyclerViewShowProfileFriends.setAdapter(adapter);
    }

    private void setOnClickListeners() {
        constraintLayoutSetUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsernameValidator validator=new UsernameValidator(editTextNewUsername.getText().toString(),getApplicationContext());
                if (validator.isValid()){
                    changeUsername(editTextNewUsername.getText().toString());
                }
            }
        });

        constraintLayoutShowProfileShowFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFriendsAndRequests.clear();
                updateFriendsOrRequests(Constants.FRIEND_REQUEST_TYPE_ACCEPTED);
            }
        });

        constraintLayoutShowProfileShowRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFriendsAndRequests.clear();
                updateFriendsOrRequests(Constants.FRIEND_REQUEST_TYPE_RECEIVED);
            }
        });

        constraintLayoutShowProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        constraintLayoutShowProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance(getApplicationContext()).startActivity(UserLoggedIn.class);
            }
        });
    }

    private void changeUsername(String newUsername) {
        referenceClean = firebaseDatabase.getReference().child("Users").child(user.getUid());
        Map map = new HashMap();
        map.put("username", newUsername);
        referenceClean.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ShowMyProfileActivity.this, "Username successfully changed.", Toast.LENGTH_SHORT).show();
                    textViewShowProfileUsername.setText(newUsername);
                }
            }
        });
    }


    private void updateFriendsOrRequests(String type) {
        adapter.notifyDataSetChanged();
        referenceFriends.child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child(Constants.CHILD_FRIEND_REQ_TYPE).getValue().toString().equals(type)) {
                    userFriendsAndRequests.add(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                }
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

    }

    private void logOut() {
        auth.signOut();
        ActivityManager.getInstance(ShowMyProfileActivity.this).startActivity(MainActivity.class);
    }
}