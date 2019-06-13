package com.mrbell.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    private FloatingActionButton addPostButton;

    private FirebaseUser currentuser;

    private BottomNavigationView main_Navigation;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       main_Navigation=findViewById(R.id.main_navigationview);
        mToolbar=findViewById(R.id.main_toolbar);
        mAuth=FirebaseAuth.getInstance();
        addPostButton=findViewById(R.id.add_post_btn);

        currentuser=mAuth.getCurrentUser();

        //Fragment initialization



        setSupportActionBar(mToolbar);
       getSupportActionBar().setTitle("PhotoBlog");

        if(mAuth.getCurrentUser()!=null){

            homeFragment=new HomeFragment();
            notificationFragment=new NotificationFragment();
            accountFragment=new AccountFragment();

       main_Navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

               switch (menuItem.getItemId()){
                   case R.id.home:
                      replaceFragment(homeFragment);
                      return true;
                   case R.id.notification:
                       replaceFragment(notificationFragment);
                       return true;
                   case R.id.account:
                       replaceFragment(accountFragment);
                       return true;
                       default:
                           return false;
               }

           }
       });



            addPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),NewPostActivity.class));
                }
            });

        }
    }

    private void senTologin() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));

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
                   if(currentuser!=null)
                       sendTosetup();
                   else
                       Toast.makeText(this, "Singin first", Toast.LENGTH_SHORT).show();

               return  true;
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
        Toast.makeText(this, "you signed out", Toast.LENGTH_SHORT).show();
        senTologin();
    }
    @Override
    protected void onStart() {
        super.onStart();

        currentuser=mAuth.getCurrentUser();
        if(currentuser==null){
            sendtTologin();
        }
    }

    private void sendtTologin() {

        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_framelayout,fragment);
        fragmentTransaction.commit();
    }
}
