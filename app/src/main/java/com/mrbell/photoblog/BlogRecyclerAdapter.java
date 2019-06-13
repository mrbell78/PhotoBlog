package com.mrbell.photoblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.CustomViewholder> {

   public List<BlogPost> blog_list;
   public Context context;
    private static final String TAG = "BlogRecyclerAdapter";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public BlogRecyclerAdapter(List<BlogPost> blog_list) {
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public CustomViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_listitem,viewGroup,false);
        context=viewGroup.getContext();
        CustomViewholder holder = new CustomViewholder(v);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewholder customViewholder, int i) {
        String desc_data = blog_list.get(i).getDesp();
       customViewholder.setDescription(desc_data);

       String imageuri= blog_list.get(i).getImage();
       customViewholder.setBlogimage(imageuri);

        Log.d(TAG, "onBindViewHolder: this is image uri"+imageuri);
        Log.d(TAG, "onBindViewHolder: this is description"+desc_data);

        String user_id=blog_list.get(i).getUser_id();


        long milliseconds =  blog_list.get(i).getTimestamp().getTime();
        CharSequence dateTime =  DateFormat.format("dd/MM/yyy", new Date(milliseconds));

        customViewholder.setTime(dateTime);


        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    String username=task.getResult().getString("name");
                    String imageuri=task.getResult().getString("image");

                    customViewholder.setProfileimagewithusername(imageuri,username);

                }else{
                    Toast.makeText(context, "imageloading failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class CustomViewholder extends RecyclerView.ViewHolder {

        ImageView mImageview;
        TextView descText,userName,time;
        View mView;
        CircleImageView imgeuri;

        public CustomViewholder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

        }

        public void setDescription(String description){
            descText = mView.findViewById(R.id.layoutdesctext);
            descText.setText(description);
        }
        public void setBlogimage(String downloaduri){

            mImageview=mView.findViewById(R.id.layoutimageshow);
            Glide.with(context).load(downloaduri).into(mImageview);

        }
        public void setTime(CharSequence timeset){
            time=mView.findViewById(R.id.layouttimetext);
            time.setText(timeset);
        }

        public void setProfileimagewithusername(String imuri,String username){

            imgeuri=mView.findViewById(R.id.layoutprofileimage);
            userName=mView.findViewById(R.id.layotheadingtext);

            Glide.with(context).load(imuri).into(imgeuri);
            userName.setText(username);

        }
    }
}
