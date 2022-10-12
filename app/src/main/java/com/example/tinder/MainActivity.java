package com.example.tinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tinder.Cards.arrayAdapter;
import com.example.tinder.Matched.MatchedActivity;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private ArrayList<String> al;
    private Model cards_data[];
    private com.example.tinder.Cards.arrayAdapter arrayAdapter;
    private int i;

    private FirebaseAuth mAuth;

    private String currentUId;
    private String currentUname;

    private DatabaseReference usersDb;
    private DatabaseReference chatDb;
    private StorageReference mStorageRef;

    private Button logoutBtn, SettingBtn, matchBtn;
    ListView listView;
    List<Model> rowItems;
    //    @InjectView(R.id.frame)
    SwipeFlingAdapterView flingContainer;

    EditText etToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        chatDb = FirebaseDatabase.getInstance().getReference().child("Chat");

        currentUId = mAuth.getCurrentUser().getUid();
        currentUname = mAuth.getCurrentUser().getDisplayName();
        checkUserSex();

        rowItems = new ArrayList<Model>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        logoutBtn = findViewById(R.id.logoutBtn);
        SettingBtn = findViewById(R.id.SettingBtn);
        matchBtn = findViewById(R.id.matchBtn);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        etToken = findViewById(R.id.etToken);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        SettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("userSex", userSex);
                startActivity(intent);
                return;
            }
        });

        matchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MatchedActivity.class);
                intent.putExtra("userSex1", userSex);
                startActivity(intent);
                return;
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast

                        System.out.println(token);
//                        Toast.makeText(MainActivity.this, "token: " + token, Toast.LENGTH_SHORT).show();
                        etToken.setText(token);
                    }
                });

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                Model obj = (Model) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeUserSex).child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Model obj = (Model) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeUserSex).child(userId).child("connections").child("yeps").child(currentUId).setValue(true);

                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(userSex).child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();

                    sendNotification("bạn có 1 tương hợp mới ");
//                    FirebaseDatabase.getInstance().getReference().child("Chat");
//                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Chat");

                    String key = FirebaseDatabase.getInstance().getReference()
                            .child("Chat").push().getKey();
//                    chatDb.child(key);
                    usersDb.child(oppositeUserSex).child(snapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(userSex).child(currentUId).child("connections").child("matches").child(snapshot.getKey()).child("ChatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String userSex;
    private String oppositeUserSex;

    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(user.getUid())) {
                    userSex = "Male";
                    oppositeUserSex = "Female";
                    getOppositeSexUsers();
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


        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(user.getUid())) {
                    userSex = "Female";
                    oppositeUserSex = "Male";
                    getOppositeSexUsers();
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

    public void getOppositeSexUsers() {
        DatabaseReference oppositeSexDb = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex);
        oppositeSexDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && !snapshot.child("connections").child("nope").hasChild(currentUId) && !snapshot.child("connections").child("yeps").hasChild(currentUId)) {
                    //cards Items = new cards(snapshot.getKey(),snapshot.child("name").getValue().toString() , snapshot.child("imageUrl").getValue().toString());
                    Model Items = new Model(snapshot.getKey(), snapshot.child("name").getValue().toString(), snapshot.child("phone").getValue().toString(), snapshot.child("imageUrl").getValue().toString());
                    rowItems.add(Items);
                    arrayAdapter.notifyDataSetChanged();
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
                .setContentTitle("My noti")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "channel human", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuiler.build());
    }


    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
        return;
    }

    public void goToMatched(View view) {
        Intent intent = new Intent(MainActivity.this, MatchedActivity.class);
        intent.putExtra("userSex1", userSex);
        startActivity(intent);
        return;
    }
}