package com.example.tinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText forEmail;
    private Button forgot;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgotpassword);

        forEmail = findViewById(R.id.forEmail);
        forgot = findViewById(R.id.forgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = forEmail.getText().toString();

                if(email.isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Please provide your email", Toast.LENGTH_SHORT).show();
                } else {
                    forgotpassword();
                }
            }
        });
    }

    private void forgotpassword() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPassword.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(ForgotPassword.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
