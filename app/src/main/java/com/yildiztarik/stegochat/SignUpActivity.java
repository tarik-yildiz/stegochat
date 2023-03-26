package com.yildiztarik.stegochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {
    EditText editTextUserName,editTextPassword,editTextName;
    ConstraintLayout constraintLayoutSignIn,constraintLayoutSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initComponents();
    }
    private void initComponents(){
        editTextName=findViewById(R.id.editTextNameRegister);
        editTextUserName=findViewById(R.id.editTextUserNameRegister);
        editTextPassword=findViewById(R.id.editTextPasswordRegister);
        constraintLayoutSignIn=findViewById(R.id.constraintLayoutSignInRegister);
        constraintLayoutSignUp=findViewById(R.id.constraintLayoutSignUpRegister);
    }
}