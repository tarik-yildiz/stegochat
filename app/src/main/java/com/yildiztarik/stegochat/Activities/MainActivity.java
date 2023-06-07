package com.yildiztarik.stegochat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.SignUpActivity;
import com.yildiztarik.stegochat.Utils.ActivityManager;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    EditText editTextMail, editTextPassword;
    ConstraintLayout constraintLayoutSignIn, constraintLayoutSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        setButtons();
    }

    private void initComponents() {
        editTextMail = findViewById(R.id.editTextLMailLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        constraintLayoutSignIn = findViewById(R.id.constraintLayoutSignInLogin);
        constraintLayoutSignUp = findViewById(R.id.constraintLayoutSignUpLogin);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            ActivityManager.getInstance(MainActivity.this).startActivity(UserLoggedIn.class);
        }
    }

    private void setButtons() {
        constraintLayoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = editTextMail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (!mail.equals("") && !password.equals("")) {
                    signIn(mail, password);
                } else {
                    Toast.makeText(MainActivity.this, "Email and pass required", Toast.LENGTH_SHORT).show();
                }
            }
        });
        constraintLayoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance(MainActivity.this).startActivity(SignUpActivity.class);
            }
        });
    }

    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                    ActivityManager.getInstance(MainActivity.this).startActivity(UserLoggedIn.class);
                } else {
                    Toast.makeText(MainActivity.this, "failed to login!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}