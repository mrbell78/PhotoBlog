package com.mrbell.photoblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setupProgress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;
    private static final String TAG = "SetupActivity";
    String downloadUri;
    String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        setupImage = findViewById(R.id.setup_profileimage);
        setupName = findViewById(R.id.setup_edtName);
        setupBtn = findViewById(R.id.setup_savebtn);
        setupProgress = findViewById(R.id.setup_progressBar);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);





            firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(task.getResult().exists()){

                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");


                            mainImageURI = Uri.parse(image);
                            Log.d(TAG, "onComplete: "+mainImageURI);
                            setupName.setText(name);

                            user_name=setupName.getText().toString();

                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.default_image);



                            Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                            //Picasso.with(SetupActivity.this).load(mainImageURI).into(setupImage);


                        }

                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                    }

                    setupProgress.setVisibility(View.INVISIBLE);
                    setupBtn.setEnabled(true);

                }
            });



            setupImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                        if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                            Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                        } else {

                            BringImagePicker();

                        }

                    } else {

                        BringImagePicker();

                    }

                }

            });





        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_name=setupName.getText().toString();

                Log.d(TAG, "onClick: imageUri"+mainImageURI);

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {

                    setupProgress.setVisibility(View.VISIBLE);


                        user_id = firebaseAuth.getCurrentUser().getUid();

                        /*File newImageFile = new File(mainImageURI.getPath());
                        try {

                            compressedImageFile = new Compressor(SetupActivity.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();*/

                       // UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                      final StorageReference image_path=storageReference.child("profile_images").child(user_id + ".jpg");

                      image_path.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                          @Override
                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                              if(taskSnapshot!=null){
                                  image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                      @Override
                                      public void onSuccess(Uri uri) {

                                          downloadUri=uri.toString();

                                          Map<String, String> userMap = new HashMap<>();
                                          userMap.put("name", user_name);
                                          userMap.put("image", downloadUri);

                                          firebaseFirestore.collection("users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {

                                                  if(task.isSuccessful()){
                                                      Toast.makeText(SetupActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
                                                      setupProgress.setVisibility(View.INVISIBLE);
                                                      startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                  }else{
                                                      Toast.makeText(SetupActivity.this, "Uploaderror "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          });


                                      }
                                  });
                              }

                          }
                      });



                }else{
                    Toast.makeText(SetupActivity.this, "filed value mising", Toast.LENGTH_SHORT).show();
                }

            }

        });




    }


    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}