package com.wisnusaputra.pinakaapps.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wisnusaputra.pinakaapps.Adapter.MessageAdapter;
import com.wisnusaputra.pinakaapps.Model.MessageMember;
import com.wisnusaputra.pinakaapps.Model.User;
import com.wisnusaputra.pinakaapps.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPrivateActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CircleImageView imageView;
    ImageView cabtn, micbtn;
    Button sendbtn;
    TextView username;
    ImageButton back, home;
    EditText chat;
    private static final int PICK_FILE = 1;

    private MessageAdapter messAdapter;

    List<MessageMember> messList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootref1, rootref2 ;
    MessageMember messageMember, messageMember1;
    String receiver_name, receiver_id, sender_id,url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_private);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            receiver_id = bundle.getString("publisherid");
        }
        else{
            Toast.makeText(this, "user missing", Toast.LENGTH_SHORT).show();
        }

        messageMember = new MessageMember();
        messageMember1 = new MessageMember();
        recyclerView = findViewById(R.id.reyclerview_message_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatPrivateActivity.this));

        userInfo();




        imageView = findViewById(R.id.image_profile);
        back = findViewById(R.id.back);
        home = findViewById(R.id.home);
        username = findViewById(R.id.txt_name);
        sendbtn = findViewById(R.id.button_chatbox_send);
        chat = findViewById(R.id.edittext_chatbox);
        cabtn = findViewById(R.id.add_media);
        micbtn = findViewById(R.id.mic);



        Glide.with(ChatPrivateActivity.this).load(url).into(imageView);
        username.setText(receiver_name);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_id = user.getUid();

        rootref1 =database.getReference("Message").child(sender_id);
        rootref2 =database.getReference("Message").child(receiver_id);

//        loadData();
        readMessage();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatPrivateActivity.this, ChatActivity.class));
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(ChatPrivateActivity.this, MainActivity.class));
                Intent intent = new Intent(ChatPrivateActivity.this, MainActivity.class);
                intent.putExtra("publisherid", receiver_id);
                startActivity(intent);
            }
        });
        cabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                it.setType("image/* video/*");
                startActivityForResult(it,PICK_FILE);
            }
        });

    }

    void SendMessage(){
        String message = chat.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate +":"+savetime;

        if(message.isEmpty()){
            Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
        }
        else{
            messageMember.setReceiveruid(savedate);
            messageMember.setTime(savetime);
            messageMember.setMessage(message);
            messageMember.setReceiveruid(receiver_id);
            messageMember.setSenderuid(sender_id);
            messageMember.setType("text");

            String id = rootref1.push().getKey();
//            messageMember.setId(id);
            rootref1.child(id).setValue(messageMember);
//            rootref1.setValue(messageMember);


//            messageMember1.setReceiveruid(savedate);
//            messageMember1.setTime(savetime);
//            messageMember1.setMessage(message);
//            messageMember1.setReceiveruid(sender_id);
//            messageMember1.setSenderuid(receiver_id);
//            messageMember1.setType("text");
            String id1 = rootref2.push().getKey();
//            messageMember.setId(id1);
            rootref2.child(id1).setValue(messageMember);
//            rootref2.setValue(messageMember);

            chat.setText("");

        }
        recyclerView.scrollToPosition(messList.size() - 1);
    }
//    void loadData(){
//        FirebaseRecyclerOptions<MessageMember> options1 =
//                new FirebaseRecyclerOptions.Builder<MessageMember>()
//                .setQuery(rootref1,MessageMember.class)
//                .build();
//
//        FirebaseRecyclerAdapter<MessageMember, MessageViewHolder> firebaseRecyclerAdapter1 =
//                new FirebaseRecyclerAdapter<MessageMember,MessageViewHolder>(options1){
//
//                    @NonNull
//                    @Override
//                    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
//                        return new MessageViewHolder(view);
//                    }
//
//                    @Override
//                    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageMember model) {
//                        holder.Setmessage(getApplication(), model.getMessage(), model.getTime(), model.getDate(),model.getType(),
//                                model.getSenderuid(), model.getReceiveruid());
//                    }
//                };
//        firebaseRecyclerAdapter1.startListening();
//        recyclerView.setAdapter(firebaseRecyclerAdapter1);
//    }

    private void readMessage(){
        rootref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MessageMember chat = snapshot.getValue(MessageMember.class);
                    if (Objects.requireNonNull(chat).getReceiveruid().equals(receiver_id) && chat.getSenderuid().equals(sender_id) ||
                            chat.getReceiveruid().equals(sender_id) && chat.getSenderuid().equals(receiver_id)){
                        messList.add(chat);
                    }

                }
                messAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(receiver_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (ChatPrivateActivity.this == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                if (user.getImageurl() != null || user.getImageurl() != ""){
                    url = user.getImageurl();
                    Glide.with(ChatPrivateActivity.this).load(user.getImageurl()).into(imageView);
                }else{
                    imageView.setImageResource(R.drawable.placeholder);
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChatPrivateActivity.this, MainActivity.class);
                        intent.putExtra("publisherid", receiver_id);
                        startActivity(intent);
                    }
                });
                messList = new ArrayList<>();
                Log.d("URLUSER111", "onBindViewHolder: " + url);
                messAdapter = new MessageAdapter(ChatPrivateActivity.this, messList, url);
                recyclerView.setAdapter(messAdapter);
//                username.setText(user.getUsername());
                username.setText(user.getFullname());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            mImageUri = result.getUri();
//
//            image_added.setImageURI(mImageUri);
//        } else {
//            startActivity(new Intent(PostActivity.this, MainActivity.class));
//            finish();
//        }

//        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data != null || data.getData() != null){
//            mImageUri = data.getData();
//            Log.d("URILOG", "onActivityResult: "+mImageUri );
//            if(mImageUri.toString().contains("IMG")){
//                image_added.setVisibility(View.VISIBLE);
//                video_added.setVisibility(View.INVISIBLE);
//                image_added.setImageURI(mImageUri);
//                type = "iv";
//            }
//            else if(mImageUri.toString().contains("mp4")){
//                video_added.setVisibility(View.VISIBLE);
//                image_added.setVisibility(View.INVISIBLE);
//                video_added.setMediaController(mediaController);
//
//                video_added.setVideoURI(mImageUri);
//                video_added.start();
//                type = "vv";
//            }else{
//                Toast.makeText(PostActivity.this, "No file selected",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(PostActivity.this, MainActivity.class));
//                finish();
//            }
//        }
    }
}