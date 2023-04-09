package com.yildiztarik.stegochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText editTextUserName,editTextPassword;
    ConstraintLayout constraintLayoutSignIn,constraintLayoutSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        setButtons();
    }

    private void initComponents(){
        editTextUserName=findViewById(R.id.editTextLUserNameLogin);
        editTextPassword=findViewById(R.id.editTextPasswordLogin);
        constraintLayoutSignIn=findViewById(R.id.constraintLayoutSignInLogin);
        constraintLayoutSignUp=findViewById(R.id.constraintLayoutSignUpLogin);
    }

    private void setButtons(){
        constraintLayoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent=new Intent(MainActivity.this, ShowChatsActivity.class);
                startActivity(signInIntent);
            }
        });
        constraintLayoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent= new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}