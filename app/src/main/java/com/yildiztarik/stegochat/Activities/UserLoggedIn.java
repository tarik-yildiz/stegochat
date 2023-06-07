package com.yildiztarik.stegochat.Activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.Adapters.UserAdapter;
import com.yildiztarik.stegochat.Utils.ActivityManager;
import com.yildiztarik.stegochat.Utils.Constants;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserLoggedIn extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    LinearLayout linearLayoutProfil;

    TextView textViewUserMail,
            textViewLoggedUserName;

    RecyclerView recyclerViewUsers;
    UserAdapter adapter;

    List<String> userKeyList;

    boolean userIsReal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_logged_in);
        initComponents();
        setOnClick();
    }

    private void initComponents() {

        linearLayoutProfil=findViewById(R.id.linearLayoutProfil);

        userIsReal = false;

        userKeyList = new ArrayList<>();

        textViewUserMail = findViewById(R.id.textViewLoggedUserMail);
        textViewLoggedUserName = findViewById(R.id.textViewLoggedUserName);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();

        setRecyclerViewUsers();

        loadUsersWithDelay();

    }

    private void loadUsersWithDelay() {
        Toast.makeText(this, "Users loading...", Toast.LENGTH_SHORT).show();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getAllUsers();
            }
        };
        timer.schedule(task, 300);
    }


    private void setRecyclerViewUsers() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(layoutManager);
        adapter = new UserAdapter(userKeyList, this, getApplicationContext());
        recyclerViewUsers.setAdapter(adapter);
    }


    private void setOnClick() {

        linearLayoutProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance(getApplicationContext()).startActivity(ShowMyProfileActivity.class);
            }
        });
    }

    private void logOut() {
        auth.signOut();
        ActivityManager.getInstance(UserLoggedIn.this).startActivity(MainActivity.class);
    }



    private void getAllUsers() {
        databaseReference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.getKey().equals(user.getUid())&&!snapshot.child("username").getValue().toString().equals("null")) {
                    userKeyList.add(snapshot.getKey());
                } else {
                    getUserInfo(snapshot);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (!snapshot.getKey().equals(user.getUid())){
                    userKeyList.add(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                }else{
                    getUserInfo(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                userKeyList.remove(snapshot.getKey().toString());
                adapter.notifyDataSetChanged();
                if (snapshot.getKey().toString().equals(user.getUid().toString())) {
                    Toast.makeText(UserLoggedIn.this, "Your account has been deleted!", Toast.LENGTH_SHORT).show();
                    logOut();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    private void getUserInfo(DataSnapshot snapshot) {
        textViewUserMail.setText(user.getEmail());
        textViewLoggedUserName.setText(snapshot.child("username").getValue().toString());
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}