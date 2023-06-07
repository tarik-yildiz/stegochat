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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yildiztarik.stegochat.R;
import com.yildiztarik.stegochat.Utils.EmailValidator;
import com.yildiztarik.stegochat.Utils.ActivityManager;
import com.yildiztarik.stegochat.Utils.PasswordValidator;
import com.yildiztarik.stegochat.Utils.RandomUsernameGenerator;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

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
        user=auth.getCurrentUser();
    }
    public void methodOnClick() {
        constraintLayoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = editTextMail.getText().toString();
                String password = editTextPassword.getText().toString();
                PasswordValidator passwordValidator=new PasswordValidator(password);
                if (mail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "username and password required", Toast.LENGTH_SHORT).show();
                }else if(!passwordValidator.isValid()){
                    String passwordConditions="The password must be 8 characters long and should contain both uppercase and lowercase letters as well as numbers.";
                    Toast.makeText(SignUpActivity.this, passwordConditions, Toast.LENGTH_SHORT).show();
                }else if(!EmailValidator.validate(mail)){
                    Toast.makeText(SignUpActivity.this, "Email format is not valid!", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(mail, password);
                }
            }
        });

        constraintLayoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance(SignUpActivity.this).startActivity(MainActivity.class);
            }
        });
    }

    public void register(String mail, String password) {
        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseDatabase=FirebaseDatabase.getInstance();
                    databaseReference =firebaseDatabase.getReference().child("Users").child(auth.getUid());
                    RandomUsernameGenerator usernameGenerator=new RandomUsernameGenerator();

                    Map map =new HashMap();
                    map.put("username",usernameGenerator.generateUsername());
                    databaseReference.setValue(map);
                    Toast.makeText(SignUpActivity.this, "Sign up Success!", Toast.LENGTH_SHORT).show();
                    ActivityManager.getInstance(SignUpActivity.this).startActivity(UserLoggedIn.class);
                } else {
                    Toast.makeText(SignUpActivity.this, "Failed to sign up!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}