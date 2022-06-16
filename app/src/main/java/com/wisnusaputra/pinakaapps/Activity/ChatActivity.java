package com.wisnusaputra.pinakaapps.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wisnusaputra.pinakaapps.Adapter.ChatAdapter;
import com.wisnusaputra.pinakaapps.Adapter.StoryAdapter;
import com.wisnusaputra.pinakaapps.Adapter.UserAdapter;
import com.wisnusaputra.pinakaapps.Model.MessageMember;
import com.wisnusaputra.pinakaapps.Model.Story;
import com.wisnusaputra.pinakaapps.Model.User;
import com.wisnusaputra.pinakaapps.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.NonNull;

public class ChatActivity extends AppCompatActivity {

    ImageButton home;
    CircleImageView image_profile;
    private RecyclerView recyclerView_story;
    private List<Story> storyList;
    private List<MessageMember> messList;
    private List<String> followingList, ListChat,checkChats, userList;
    private StoryAdapter storyAdapter;
    private List<User> chatList;
    private ChatAdapter chatAdapter;

    DatabaseReference profileRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    EditText searchEt;

    String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messList = new ArrayList<>();
        userList = new ArrayList<>();
        ListChat = new ArrayList<>();

        image_profile = findViewById(R.id.image_profile);
        home = findViewById(R.id.home);
        recyclerView_story = findViewById(R.id.recyclerView_story);

        searchEt = findViewById(R.id.searchuschat);
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));


        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(ChatActivity.this, storyList);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatActivity.this, chatList, true);

        recyclerView_story.setAdapter(storyAdapter);
        recyclerView.setAdapter(chatAdapter);

        profileRef = database.getReference("Users");



        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users");
        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(ChatActivity.this).load(dataSnapshot.child(idUser).getValue(User.class).getImageurl()).into(image_profile);
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String user = snapshot.getValue(User.class).getId();
                        userList.add(user);
                    }

                checkFollowing();
                }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
            }
        });



    }
    void checkMess(){
        DatabaseReference referenceUser1 = FirebaseDatabase.getInstance().getReference("Message").child(idUser);
        referenceUser1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageMember mess = snapshot.getValue(MessageMember.class);
                    messList.add(mess);
                }
//
                for (MessageMember mess : messList){

                    for (String id : checkChats){
                        if((mess.getSenderuid().equals(idUser) && mess.getReceiveruid().equals(id)) ||
                                (mess.getSenderuid().equals(id) && mess.getReceiveruid().equals(idUser))){
                            if(ListChat.size() == 0){
                                ListChat.add(id);
                            }
                            else{
                                for (String idcheck: ListChat){
                                    if(!idcheck.equals(id)){
                                        ListChat.add(id);
                                    }
                                }
                            }

                        }
                    }
                }
                readUsers();

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followingList) {
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                        story = snapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        storyList.add(story);
                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        checkChats = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingList.clear();
                checkChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                for(String id : userList){
                    boolean kt = true;
                    for(String id1 : followingList){
                        if(id.equals(id1)){
                            kt = false;
                            break;
                        }
                    }
                    if(kt){
                        checkChats.add(id);
                    }
               }

                checkMess();

                Log.d("SLCHAT1", "onDataChange: "+ ListChat.size());
                readStory();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void searchUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    chatList.add(user);
                }

                chatAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    void checkChat(String id){
//
//        DatabaseReference rootref1 = database.getReference("Message").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id);
//        rootref1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
//                boolean kt = true;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Log.d("SLCHAT2", "onDataChange: "+ snapshot);
//                    if(snapshot != null){
//                        ListChat.add(snapshot.getKey());
//                        kt = false;
//                        Log.d("SLCHAT3", "onDataChange: "+ ListChat.size());
//                        break;
//                    }
//                }
//                if(kt){
//                    DatabaseReference rootref2= database.getReference("Message").child(id).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    rootref2.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                                if(snapshot != null){
//                                    ListChat.add(snapshot.getKey());
//                                    break;
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
//
//            }
//        });
//        Log.d("SLCHAT4", "onDataChange: "+ ListChat.size());
//
//    }
    private void readUsers() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchEt.getText().toString().equals("")) {
                    chatList.clear();
                    checkChats = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        for (String id : ListChat){
                            if (user.getId().equals(id)){
                                chatList.add(user);
                            }
                        }
                        for (String id : followingList){
                            if (user.getId().equals(id)){
                                chatList.add(user);
                            }
                        }

                    }

                    chatAdapter.notifyDataSetChanged();
                }
//                for (String id : followingList) {
//                    chatList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
//                        User user = snapshot.getValue(User.class);
//                        chatList.add(user);
//
//                    }
//                    Log.d("ChatList", "onDataChange: " + chatList.size());
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
