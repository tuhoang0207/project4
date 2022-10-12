package com.example.tinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegister;

    private EditText mEmail, mPassword, mName;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    boolean passwordVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        // kiểm tra xem có nick nào đang đăng nhập không , có thì chuyển sang main
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();


        mRegister = (Button) findViewById(R.id.button4);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if (motionEvent.getAction() == motionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= mPassword.getRight() - mPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = mPassword.getSelectionEnd();
                        if (passwordVisible) {
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        mPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int selectId = mRadioGroup.getCheckedRadioButtonId();

                //validate email
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                final RadioButton radioButton = (RadioButton) findViewById(selectId);
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();




                //validate


                if (email.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "email mustn't empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    Toast.makeText(RegistrationActivity.this, "password mustn't empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "name mustn't empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegistrationActivity.this, "gender mustn't empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //kiểm tra email có đúng định dạng @gmail.com không
                if (email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"please type again your email",Toast.LENGTH_SHORT).show();
                    return;
                }

                mPassword.setTransformationMethod(new PasswordTransformationMethod());
                // tạo account với email và password
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) { //fall
                            Toast.makeText(RegistrationActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                        } else {
                            String userId = mAuth.getCurrentUser().getUid();
                            // tạo Users/ giới tính mình chọn/id user vừa tạo
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId);
                            // nhét key và value vào Map để update db
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("sex", radioButton.getText().toString());
                            userInfo.put("phone", "");
                            userInfo.put("imageUrl", "default");
                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}