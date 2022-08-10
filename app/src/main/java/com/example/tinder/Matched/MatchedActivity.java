package com.example.tinder.Matched;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchedActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
//    private String userSex;
    private String currentUserId;

    private TextView userSex;

//    private TextView mSexTextView;

    private FirebaseAuth mAuth;

    private String currentUId;
    private  String currentUname;

    private DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference usersDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched);

//        mSexTextView.setText("123");
//        String a = (String) mSexTextView.getText();

        String username = getIntent().getExtras().getString("username");

//        userSex = (TextView) findViewById(R.id.MatchSex2);

//        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchedActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchedActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
        getUserMatchId();

    }


    private void getUserMatchId() {
        String userSex1 = getIntent().getExtras().getString("userSex1");
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userSex1)
                .child(currentUserId)
                .child("connections")
                .child("matches");
        //thu xem nao, cho nay khiep the :D
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){ //loi o day a
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FecthMatchInformation(match.getKey());
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String gender;
    private void FecthMatchInformation(String key) {
        String userSex1 = getIntent().getExtras().getString("userSex1");
        if (userSex1.equals("Male")) {
            gender = "Female";
        } else if(userSex1.equals("Female")) {
            gender = "Male";
        } else {

        }
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(gender).child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userId = snapshot.getKey();
                    String name = "";
                    String imageUrl = "";
                    String sex = "";
                    if (snapshot.child("name").getValue() != null) {
                        name = snapshot.child("name").getValue().toString();
                    }
                    if (snapshot.child("imageUrl").getValue() != null) {
                        imageUrl = snapshot.child("imageUrl").getValue().toString();
                    }
//                    if (snapshot.child("sex").getValue() != null) {
//                        sex = snapshot.child("sex").getValue().toString();
//                    }

                    MatchesObject obj = new MatchesObject(userId,name,imageUrl);
                    resultMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultMatches;
    }
}