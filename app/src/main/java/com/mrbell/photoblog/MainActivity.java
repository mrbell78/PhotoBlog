package com.mrbell.photoblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btn_click;

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth.getInstance().signOut();
        btn_click=findViewById(R.id.btn_click_main);
        mToolbar=findViewById(R.id.main_toolbar);
        mAuth=FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
       getSupportActionBar().setTitle("PhotoBlog");


        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senTologin();
            }
        });


    }

    private void senTologin() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menue,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.logout:
               logout();
               return true;
           case R.id.settings:
               sendTosetup();
               default:
                   return false;

       }
    }

    private void sendTosetup() {
        startActivity(new Intent(getApplicationContext(),SetupActivity.class));
        finish();
    }

    private void logout() {

        mAuth.signOut();
        senTologin();
    }
}
