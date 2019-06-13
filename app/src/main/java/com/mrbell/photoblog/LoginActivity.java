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

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail,loginPassword;
    private Button btn_Login,btn_CreateAccount;
    private ProgressBar loginProgressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        loginEmail=findViewById(R.id.login_edtEmail);
        loginPassword=findViewById(R.id.login_edtPassword);
        btn_Login=findViewById(R.id.login_signin_btn);
        btn_CreateAccount=findViewById(R.id.login_CreateAccount);
        loginProgressBar=findViewById(R.id.login_progressBar);

        btn_CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToregister();
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=loginEmail.getText().toString();
                String pasword=loginPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pasword)){

                    loginProgressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email,pasword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){



                                Toast.makeText(LoginActivity.this, "Congrats! u signed in", Toast.LENGTH_SHORT).show();
                                loginProgressBar.setVisibility(View.INVISIBLE);
                               sendTomain();

                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Login Failed "+ message, Toast.LENGTH_SHORT).show();
                                loginProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

    }

    private void sendTosetting() {
        startActivity(new Intent(getApplicationContext(),SetupActivity.class));
    }

    private void sendToregister() {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser!=null){
            sendTomain();
        }
    }

    private void sendTomain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
}
}
