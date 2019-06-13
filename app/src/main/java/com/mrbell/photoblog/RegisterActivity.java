package com.mrbell.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail,inputPassword,inputConfirmPassword;
    private Button btn_CreateAccount,btn_AccountExist;
    private ProgressBar register_progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail=findViewById(R.id.register_edtEmail);
        inputPassword=findViewById(R.id.register_edtPassword);
        inputConfirmPassword=findViewById(R.id.register_edtConfirmpassword);
        btn_CreateAccount=findViewById(R.id.register_CreateAccount);
        btn_AccountExist=findViewById(R.id.register_Accountexist);
        register_progressBar=findViewById(R.id.register_progressBar);
         mAuth=FirebaseAuth.getInstance();

        btn_AccountExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTologin();
            }
        });

         btn_CreateAccount.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String email = inputEmail.getText().toString();
                 String password=inputPassword.getText().toString();
                 String confirmpass=inputConfirmPassword.getText().toString();

                 if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmpass)){
                        register_progressBar.setVisibility(View.VISIBLE);
                     if(password.equals(confirmpass)){
                         mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 if(task.isSuccessful()){
                                    sendTomain();
                                     Toast.makeText(RegisterActivity.this, "Registration Succesful", Toast.LENGTH_SHORT).show();
                                     register_progressBar.setVisibility(View.INVISIBLE);
                                 }else{
                                     String messatge = task.getException().toString();
                                     register_progressBar.setVisibility(View.INVISIBLE);
                                     Toast.makeText(RegisterActivity.this, "Registration Failed "+messatge, Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
                     }else{

                         Toast.makeText(RegisterActivity.this, "passwords doesnt match", Toast.LENGTH_SHORT).show();
                         register_progressBar.setVisibility(View.INVISIBLE);
                     }
                 }
             }
         });


    }

    private void sendTologin() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            sendTomain();
        }
    }

    private void sendTomain() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
