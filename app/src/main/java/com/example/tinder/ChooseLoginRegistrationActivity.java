package com.example.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {
    private Button mLogin,mRegister;
    private DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        mLogin = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

//        currentUserDb.child("Users").child("Users").child("Male").child("265").child("name").setValue("tu");
//        currentUserDb.child("Users").child("Users").child("Female").child("2").child("name").setValue("Tu2");

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this,RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
}