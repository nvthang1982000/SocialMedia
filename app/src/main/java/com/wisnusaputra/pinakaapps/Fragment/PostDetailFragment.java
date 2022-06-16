package com.wisnusaputra.pinakaapps.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wisnusaputra.pinakaapps.Activity.MainActivity;
import com.wisnusaputra.pinakaapps.Adapter.PostDetailAdapter;
import com.wisnusaputra.pinakaapps.Model.Post;
import com.wisnusaputra.pinakaapps.R;

import static android.content.Context.MODE_PRIVATE;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {



    String postid;

    private RecyclerView recyclerView;
    private PostDetailAdapter postAdapter;
    private List<Post> postList;
    ImageView back_home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = prefs.getString("postid", "none");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        back_home = view.findViewById(R.id.back_home);
        back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        postList = new ArrayList<>();
        postAdapter = new PostDetailAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        readPost();

        return view;
    }

    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
