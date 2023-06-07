package com.yildiztarik.stegochat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.Adapters.MessageAdapter;
import com.yildiztarik.stegochat.Models.MessageModel;
import com.yildiztarik.stegochat.Utils.ActivityManager;
import com.yildiztarik.stegochat.Utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    TextView textViewChatUsername;
    RecyclerView recylerViewChatMessageList;
    MessageAdapter adapter;
    List<MessageModel> messageModelList;
    List<String> bosliste;

    EditText editTextChatMessageInput;
    ImageButton imageButtonChatSendMessage, imageButtonChatSendImage;
    String showingUserId;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference, cleanReference, friendReference;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        showingUserId = intent.getStringExtra(Constants.USER_ID);
        initComponents();
        getUserInfo();
        setOnClick();
        loadMessages();
        setRecyclerView();
    }

    private void initComponents() {
        messageModelList=new ArrayList<>();
        bosliste=new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userReference = firebaseDatabase.getReference().child(Constants.CHILD_USERS);
        cleanReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textViewChatUsername = findViewById(R.id.textViewChatUsername);
        recylerViewChatMessageList = findViewById(R.id.recylerViewChatMessageList);
        editTextChatMessageInput = findViewById(R.id.editTextChatMessageInput);
        imageButtonChatSendMessage = findViewById(R.id.imageButtonChatSendMessage);
        imageButtonChatSendImage = findViewById(R.id.imageButtonChatSendImage);

    }


    private void getUserInfo() {
        userReference.child(showingUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                textViewChatUsername.setText(snapshot.getValue().toString());
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

    private void sendMessage(String userId, String showingUserId, String messageType, String date, String message) {
        String messageId = cleanReference.child(Constants.CHILD_MESSAGES).child(userId).child(showingUserId).push().getKey();
        Map messageItem = new HashMap();
        messageItem.put(Constants.CHILD_MESSAGE_TYPE, messageType);
        messageItem.put(Constants.CHILD_MESSAGE_DATE, date);
        messageItem.put(Constants.CHILD_MESSAGE_TEXT, message);
        messageItem.put(Constants.CHILD_MESSAGE_FROM, userId);

        cleanReference.child(Constants.CHILD_MESSAGES).child(userId).child(showingUserId).child(messageId).setValue(messageItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    cleanReference.child(Constants.CHILD_MESSAGES).child(showingUserId).child(userId).child(messageId).setValue(messageItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            }
        });

    }

    private void setOnClick() {
        imageButtonChatSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance(getApplicationContext()).startActivityWithMessage(EncodeDecodePhotoActivity.class, Constants.OTHER_ID, showingUserId);
            }
        });
        imageButtonChatSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextChatMessageInput.getText().toString().trim();
                if (!message.isEmpty() && !message.equals(" ")) {
                    editTextChatMessageInput.setText("");
                    sendMessage(user.getUid(), showingUserId, Constants.CHILD_MESSAGE_TYPE_TEXT, getDate(), message);
                }
            }
        });
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
    private void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recylerViewChatMessageList.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(bosliste, this, getApplicationContext(),messageModelList);
        recylerViewChatMessageList.setAdapter(adapter);
    }
    private void loadMessages(){
        cleanReference.child(Constants.CHILD_MESSAGES).child(user.getUid()).child(showingUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageModel messageModel=new MessageModel();
                messageModel.setFrom(snapshot.child(Constants.CHILD_MESSAGE_FROM).getValue().toString());
                messageModel.setDate(snapshot.child(Constants.CHILD_MESSAGE_DATE).getValue().toString());
                messageModel.setUserId(user.getUid());
                messageModel.setOtherId(showingUserId);
                messageModel.setMessageType(snapshot.child(Constants.CHILD_MESSAGE_TYPE).getValue().toString());
                messageModel.setMessage(snapshot.child(Constants.CHILD_MESSAGE_TEXT).getValue().toString());
                messageModelList.add(messageModel);
                adapter.notifyDataSetChanged();
                recylerViewChatMessageList.scrollToPosition(messageModelList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                messageModelList.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}