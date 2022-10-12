package com.example.tinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {
    private Button mLogin,mRegister;
    private DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_login_registration);

        // gán biến với id
        mLogin = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

        // bấm vào nút login thì mở sang trang login
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        // bấm vào nút register thì mở sang trang register
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