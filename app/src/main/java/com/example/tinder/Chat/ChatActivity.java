package com.example.tinder.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.tinder.MainActivity;
import com.example.tinder.Matched.MatchesAdapter;
import com.example.tinder.Matched.MatchesObject;
import com.example.tinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;

    private Button mSendButton;
    private String userSex;
    private String oppositeUserSex;
    private String currentUserId, currentUserName, matchId, chatId, sex;

    private FirebaseAuth mAuth;

    DatabaseReference mDatabaseUser, mDatabaseChat, dbMale, dbFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        getUserInfo();


        matchId = getIntent().getExtras().getString("matchId");


//        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(currentUserId).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        dbMale = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(currentUserId).child("connections").child("matches").child(matchId).child("ChatId");
        dbFemale = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(currentUserId).child("connections").child("matches").child(matchId).child("ChatId");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);
        getChatId();

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }


    private void getUserInfo() {
        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Male").child(currentUserId);
        maleDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("sex").equals("Male")) {
                        userSex = "Male";
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Female").child(currentUserId);
        femaleDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("sex").equals("Female")) {
                        userSex = "Female";
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();

        if (!sendMessageText.isEmpty()) {
            DatabaseReference newMessageDb = mDatabaseChat.push();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            String currentDateandTime = sdf.format(new Date());
            Map newMassge = new HashMap();
            newMassge.put("sendBy", currentUserId);
            newMassge.put("text", sendMessageText);
            newMassge.put("date", currentDateandTime);
            newMessageDb.setValue(newMassge);
        }
        mSendEditText.setText(null);


    }

    private void getChatId() {

        dbMale.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatId = snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbFemale.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatId = snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatMessage() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String message = null;
                    String sendBy = null;

                    if (snapshot.child("text").getValue() != null) {
                        message = snapshot.child("text").getValue().toString();
                    }
                    if (snapshot.child("sendBy").getValue() != null) {
                        sendBy = snapshot.child("sendBy").getValue().toString();
                    }


                    if (message != null && sendBy != null) {
                        Boolean currentUserBoolean = false;
                        if (sendBy.equals(currentUserId)) {
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message,currentUserBoolean);
                        if (!snapshot.child("sendBy").getValue().equals(currentUserId)) {
                            sendNotification(message);
                        }
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
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

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "My channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuiler = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_motification)
                .setContentTitle("Thong bao")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "channel human", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0,notificationBuiler.build());
    }

    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();

    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }
}