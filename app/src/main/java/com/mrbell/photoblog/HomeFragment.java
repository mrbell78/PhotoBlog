package com.mrbell.photoblog;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blog_list_view;

    private List<BlogPost> blog_list;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;
    BlogRecyclerAdapter adapter;
    DocumentSnapshot lastview;

    public HomeFragment() {
        // Required empty public constructor


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_home, container, false);

             blog_list=new ArrayList<>();
             firebaseAuth=FirebaseAuth.getInstance();

             blog_list_view=view.findViewById(R.id.homefragmentrecyclerview);
             firebaseAuth=FirebaseAuth.getInstance();



        adapter  = new BlogRecyclerAdapter(blog_list);

             blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
             blog_list_view.setAdapter(adapter);
             blog_list_view.setHasFixedSize(true);

             //Collect data form firebase

             firebaseFirestore=FirebaseFirestore.getInstance();

             blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                 @Override
                 public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                     super.onScrolled(recyclerView, dx, dy);
                     Boolean reachBottom = !recyclerView.canScrollVertically(1);

                     if(reachBottom){
                         String desc = lastview.getString("desp");
                         Toast.makeText(container.getContext(), "reached"+ desc, Toast.LENGTH_SHORT).show();
                         loadMorepost();
                     }
                 }
             });

        Query firesquery =firebaseFirestore.collection("posted_details").orderBy("timestamp",Query.Direction.DESCENDING).limit(3);
        firesquery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                     @Override
                     public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(!queryDocumentSnapshots.isEmpty()){
                            lastview=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                            for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                                if(doc.getType()==DocumentChange.Type.ADDED){
                                    BlogPost blogPost =  doc.getDocument().toObject(BlogPost.class);
                                    blog_list.add(blogPost);
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        }
                     }
                 });






        return  view;
    }

    public void loadMorepost(){
        Query firesquery =firebaseFirestore.collection("posted_details").orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastview);
        firesquery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()){

                    lastview=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                        if(doc.getType()==DocumentChange.Type.ADDED){
                            BlogPost blogPost =  doc.getDocument().toObject(BlogPost.class);
                            blog_list.add(blogPost);
                            adapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });
    }

}
