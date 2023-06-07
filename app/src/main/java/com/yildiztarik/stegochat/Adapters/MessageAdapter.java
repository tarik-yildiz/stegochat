package com.yildiztarik.stegochat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.Activities.EncodeDecodePhotoActivity;
import com.yildiztarik.stegochat.Models.MessageModel;
import com.yildiztarik.stegochat.Utils.ActivityManager;
import com.yildiztarik.stegochat.Utils.Constants;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    final int STATE_MESSAGE_RECEIVED_TEXT = 1;
    final int STATE_MESSAGE_RECEIVED_IMAGE = 2;
    final int STATE_MESSAGE_SENT_TEXT = 3;
    final int STATE_MESSAGE_SENT_IMAGE = 4;

    int state;

    List<String> userKeyList;
    List<MessageModel> messageModelList;
    Activity activity;
    Context context;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;

    public MessageAdapter(List<String> userKeyList, Activity activity, Context context, List<MessageModel> messageModelList) {
        this.userKeyList = userKeyList;
        this.messageModelList = messageModelList;
        this.activity = activity;
        this.context = context;

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        state = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        state = viewType;
        if (viewType == STATE_MESSAGE_SENT_TEXT) {
            // Sent text message
            view = LayoutInflater.from(context).inflate(R.layout.sent_text_layout, viewGroup, false);
        } else if (viewType == STATE_MESSAGE_SENT_IMAGE) {
            // Sent image message
            view = LayoutInflater.from(context).inflate(R.layout.sent_image_layout, viewGroup, false);
        } else if (viewType == STATE_MESSAGE_RECEIVED_TEXT) {
            // Received text message
            view = LayoutInflater.from(context).inflate(R.layout.received_text_layout, viewGroup, false);
        } else if (viewType == STATE_MESSAGE_RECEIVED_IMAGE) {
            // Received image message
            view = LayoutInflater.from(context).inflate(R.layout.received_image_layout, viewGroup, false);
        } else {
            // Default view
            view = null;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.initComponentsToItemViewType(state);
        MessageModel model = messageModelList.get(position);
        String temp="";
        temp=model.getMessage();
        if (state == STATE_MESSAGE_SENT_TEXT || state == STATE_MESSAGE_RECEIVED_TEXT) {
            viewHolder.textViewTextMessage.setText(temp);
        } else if (state == STATE_MESSAGE_SENT_IMAGE || state == STATE_MESSAGE_RECEIVED_IMAGE) {
            Picasso.get().load(model.getMessage()).into(viewHolder.imageViewImgMessage);
            if (state == STATE_MESSAGE_RECEIVED_IMAGE){
                viewHolder.imageViewImgMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityManager.getInstance(context).startActivityWithMessage(EncodeDecodePhotoActivity.class,Constants.IMAGE_URL,model.getMessage());
                    }
                });
            }

        } else {
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageModelList.get(position);

        if (message.getFrom().equals(message.getUserId())) {
            if (message.getMessageType().equals(Constants.CHILD_MESSAGE_TYPE_TEXT)) {
                return STATE_MESSAGE_SENT_TEXT;
            } else if (message.getMessageType().equals(Constants.CHILD_MESSAGE_TYPE_IMG)) {
                return STATE_MESSAGE_SENT_IMAGE;
            }
        } else {
            if (message.getMessageType().equals(Constants.CHILD_MESSAGE_TYPE_TEXT)) {
                return STATE_MESSAGE_RECEIVED_TEXT;
            } else if (message.getMessageType().equals(Constants.CHILD_MESSAGE_TYPE_IMG)) {
                return STATE_MESSAGE_RECEIVED_IMAGE;
            }
        }
        return -1;

    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTextMessage;
        ImageView imageViewImgMessage;
        View myView;

        public ViewHolder(View view) {
            super(view);
            myView = view;

        }

        public void initComponentsToItemViewType(int viewType) {
            if (viewType == STATE_MESSAGE_SENT_TEXT) {
                textViewTextMessage = myView.findViewById(R.id.textViewSentTextMessage);
            } else if (viewType == STATE_MESSAGE_SENT_IMAGE) {
                imageViewImgMessage = myView.findViewById(R.id.imageViewSentImage);
            } else if (viewType == STATE_MESSAGE_RECEIVED_TEXT) {
                textViewTextMessage = myView.findViewById(R.id.textViewReceivedTextMessage);
            } else if (viewType == STATE_MESSAGE_RECEIVED_IMAGE) {
                imageViewImgMessage = myView.findViewById(R.id.imageViewReceivedImage);

            } else {

            }
        }
    }
}
