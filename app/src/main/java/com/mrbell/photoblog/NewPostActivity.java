package com.mrbell.photoblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private ImageView postImage;
    private EditText  descriptionedtText;
    private Button btn_Post;
    private Toolbar mToolbar;
    private Uri postImageuri;
    private ProgressBar newPostProgressBar;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private StorageReference storageReference;

    private String currentUser_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        postImage=findViewById(R.id.newPost_imageview);
        descriptionedtText=findViewById(R.id.newPost_edtDescription);
        btn_Post=findViewById(R.id.newPostbtn);
        mToolbar=findViewById(R.id.newPost_Toolbar);
        newPostProgressBar=findViewById(R.id.newPostProgressBar);

        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        currentUser_id=mAuth.getCurrentUser().getUid();

        storageReference= FirebaseStorage.getInstance().getReference();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add new Post");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(NewPostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }else{
                        BringImagePicker();
                    }
                }else{
                    BringImagePicker();
                }

            }
        });

        btn_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String des=descriptionedtText.getText().toString();
                if(!TextUtils.isEmpty(des) && postImageuri!=null){
                    newPostProgressBar.setVisibility(View.VISIBLE);

                    String randomName= UUID.randomUUID().toString();

                    final StorageReference ref = storageReference.child("posted_image").child(randomName+".jpg");

                    ref.putFile(postImageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot!=null){
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        postImageuri=uri;

                                        Map<String,Object> userMap = new HashMap<>();

                                        userMap.put("desp",des);
                                        userMap.put("image",postImageuri.toString());
                                        userMap.put("user_id",currentUser_id);
                                        userMap.put("timestamp",FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("posted_details").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if(task.isSuccessful()){
                                                    newPostProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(NewPostActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                                    Intent mainintent = new Intent(NewPostActivity.this,MainActivity.class);
                                                    startActivity(mainintent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(NewPostActivity.this, "upload error"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });

                }else{
                    newPostProgressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
                .setAspectRatio(1, 1)
                .start(NewPostActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageuri = result.getUri();
                postImage.setImageURI(postImageuri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
