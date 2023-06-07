package com.yildiztarik.stegochat.Activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.yildiztarik.stegochat.Utils.ActivityManager;
import com.yildiztarik.stegochat.Utils.Constants;

public class ShowOtherUserActivity extends AppCompatActivity {
    String showingUserId = "error";
    TextView textViewUsernameProfile, textViewInlineAddFriend;
    ConstraintLayout constraintLayoutAddFriend,
            constraintLayoutBlockUser,
            constraintLayoutBack,
            constraintLayoutStartChat;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference, friendReqReference, friendReference;

    FirebaseAuth auth;
    FirebaseUser user;

    int friendStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_other_user);
        Intent intent = getIntent();
        showingUserId = intent.getStringExtra(Constants.USER_ID);

        initComponents();
        getUserInfo();
        setOnClick();
        controlFriendRequest();

    }


    private void initComponents() {
        friendStatus = Constants.FRIEND_REQ_NULL;
        firebaseDatabase = FirebaseDatabase.getInstance();

        userReference = firebaseDatabase.getReference().child(Constants.CHILD_USERS);
        friendReqReference = firebaseDatabase.getReference().child(Constants.CHILD_FRIEND_REQUEST);
        friendReference = firebaseDatabase.getReference().child(Constants.CHILD_FRIENDS);

        constraintLayoutAddFriend = findViewById(R.id.constraintLayoutAddFriend);
        constraintLayoutBlockUser = findViewById(R.id.constraintLayoutBlockUser);
        constraintLayoutBack = findViewById(R.id.constraintLayoutBack);
        constraintLayoutStartChat = findViewById(R.id.constraintLayoutStartChat);

        textViewUsernameProfile = findViewById(R.id.textViewShowProfileUsername);
        textViewInlineAddFriend = findViewById(R.id.textViewInlineAddFriend);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    private void getUserInfo() {
        userReference.child(showingUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                textViewUsernameProfile.setText(snapshot.getValue().toString());
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


    private void setOnClick() {
        constraintLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance(getApplicationContext()).startActivity(UserLoggedIn.class);
            }
        });

        constraintLayoutAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendStatus == Constants.FRIEND_REQ_NULL) {
                    sendFriendRequest(showingUserId);
                } else if (friendStatus == Constants.FRIEND_REQ_SENT) {
                    cancelFriendRequest(showingUserId);
                } else if (friendStatus == Constants.FRIEND_REQ_RECEIVED) {
                    acceptRequest(showingUserId);
                    Toast.makeText(ShowOtherUserActivity.this, "accepted.", Toast.LENGTH_SHORT).show();
                } else if (friendStatus == Constants.FRIENDS) {
                    Toast.makeText(ShowOtherUserActivity.this, "removed friend", Toast.LENGTH_SHORT).show();
                }
            }
        });

        constraintLayoutStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendStatus != Constants.FRIENDS) {
                    Toast.makeText(ShowOtherUserActivity.this, "Need to be friends to chat.", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityManager.getInstance(getApplicationContext()).startActivityWithMessage(ChatActivity.class,Constants.USER_ID,showingUserId);
                }
            }
        });
    }

    private void acceptRequest(String showingUserId) {
        friendReqReference.child(user.getUid()).child(showingUserId).child(Constants.CHILD_FRIEND_REQ_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendReqReference.child(showingUserId).child(user.getUid()).child(Constants.CHILD_FRIEND_REQ_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ShowOtherUserActivity.this, "accepted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowOtherUserActivity.this, "ERRROR!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendFriendRequest(String userId) {
        friendReqReference.child(user.getUid()).child(userId).child(Constants.CHILD_FRIEND_REQ_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_SENT).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendReqReference.child(userId).child(user.getUid()).child(Constants.CHILD_FRIEND_REQ_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_RECEIVED).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendStatus = Constants.FRIEND_REQ_SENT;
                            } else {
                                Toast.makeText(ShowOtherUserActivity.this, "ERRROR!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void cancelFriendRequest(String userId) {
        friendReqReference.child(user.getUid()).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                friendReqReference.child(userId).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ShowOtherUserActivity.this, "Request Cancelled!", Toast.LENGTH_SHORT).show();
                        friendStatus = Constants.FRIEND_REQ_NULL;

                    }
                });
            }
        });

    }

    private void controlFriendRequest() {
        Log.d(Constants.DEBUG_TAG, "controlFriendRequest: controlling"+friendStatus);
        friendReqReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(showingUserId)) {
                    if (snapshot.child(showingUserId).child(Constants.CHILD_FRIEND_REQ_TYPE).getValue().toString().equals(Constants.FRIEND_REQUEST_TYPE_SENT)) {
                        friendStatus = Constants.FRIEND_REQ_SENT;
                        textViewInlineAddFriend.setText("Cancel Request");
                    } else if (snapshot.child(showingUserId).child(Constants.CHILD_FRIEND_REQ_TYPE).getValue().toString().equals(Constants.FRIEND_REQUEST_TYPE_RECEIVED)) {
                        textViewInlineAddFriend.setText("Accept Request");
                        friendStatus = Constants.FRIEND_REQ_RECEIVED;
                    } else if (snapshot.child(showingUserId).child(Constants.CHILD_FRIEND_REQ_TYPE).getValue().toString().equals(Constants.FRIEND_REQUEST_TYPE_ACCEPTED)) {
                        textViewInlineAddFriend.setText("Delete Friend");
                        friendStatus = Constants.FRIENDS;
                    }
                } else {
                    friendStatus = Constants.FRIEND_REQ_NULL;
                    textViewInlineAddFriend.setText("Add Friend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}