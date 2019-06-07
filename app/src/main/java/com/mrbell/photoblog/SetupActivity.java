package com.mrbell.photoblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView profileImage;
    private EditText setupName;
    private Button btn_setting;
    private ProgressBar setupProgressBar;
    private Uri mainImageuri=null;

    private StorageReference storagerf;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mToolbar=findViewById(R.id.setup_toolbar);

        setSupportActionBar(mToolbar);
        profileImage=findViewById(R.id.setup_profileimage);
        getSupportActionBar().setTitle("PhotoBlog");

        mAuth=FirebaseAuth.getInstance();
        storagerf= FirebaseStorage.getInstance().getReference();
        setupName=findViewById(R.id.setup_edtName);
        btn_setting=findViewById(R.id.setup_savebtn);
        setupProgressBar=findViewById(R.id.setup_progressBar);


        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String user_name=setupName.getText().toString();
                    if(!TextUtils.isEmpty(user_name) && mainImageuri!=null){
                        setupProgressBar.setVisibility(View.VISIBLE);

                        StorageReference image_path = storagerf.child("profile_image").child(System.currentTimeMillis()+".jpg");

                        image_path.putFile(mainImageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){

                                    String downoad_uri = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                    Toast.makeText(SetupActivity.this, "iamge uplod successful", Toast.LENGTH_SHORT).show();
                                }else{
                                    String message = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "error"+ message, Toast.LENGTH_SHORT).show();

                                }
                                setupProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }
                    else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,2)
                                .start(SetupActivity.this);
                    }
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 mainImageuri = result.getUri();
                 profileImage.setImageURI(mainImageuri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
