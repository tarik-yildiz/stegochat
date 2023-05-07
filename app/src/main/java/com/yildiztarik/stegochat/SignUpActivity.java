package com.yildiztarik.stegochat;

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

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText editTextMail, editTextPassword;
    ConstraintLayout constraintLayoutSignIn, constraintLayoutSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initComponents();
        methodOnClick();
    }

    private void initComponents() {
        editTextMail = findViewById(R.id.editTextMailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);

        constraintLayoutSignIn = findViewById(R.id.constraintLayoutSignInRegister);
        constraintLayoutSignUp = findViewById(R.id.constraintLayoutSignUpRegister);

        auth = FirebaseAuth.getInstance();
    }

    public void methodOnClick() {
        constraintLayoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = editTextMail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (mail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "username and password required", Toast.LENGTH_SHORT).show();
                }else if(password.length()<=5){
                    Toast.makeText(SignUpActivity.this, "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }else if(!EmailValidator.validate(mail)){
                    Toast.makeText(SignUpActivity.this, "Email format is not valid!", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(mail, password);
                }
            }
        });
    }

    public void register(String mail, String password) {
        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}