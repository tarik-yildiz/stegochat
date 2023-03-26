package com.yildiztarik.stegochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText editTextUserName,editTextPassword;
    ConstraintLayout constraintLayoutSignIn,constraintLayoutSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents(){
        editTextUserName=findViewById(R.id.editTextLUserNameLogin);
        editTextPassword=findViewById(R.id.editTextPasswordLogin);
        constraintLayoutSignIn=findViewById(R.id.constraintLayoutSignInLogin);
        constraintLayoutSignUp=findViewById(R.id.constraintLayoutSignUpLogin);
    }
}