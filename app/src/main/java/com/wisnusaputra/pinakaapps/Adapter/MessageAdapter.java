package com.wisnusaputra.pinakaapps.Adapter;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wisnusaputra.pinakaapps.Activity.ChatPrivateActivity;
import com.wisnusaputra.pinakaapps.Model.MessageMember;
import com.wisnusaputra.pinakaapps.Model.User;
import com.wisnusaputra.pinakaapps.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    RelativeLayout rowmessuser,rowmesscurr;
    private Context mContext;
    private List<MessageMember> mUsers;
    String  urlfr;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public MessageAdapter(Context context, List<MessageMember> users, String urlfrs){
        mContext = context;
        mUsers = users;
        urlfr = urlfrs;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_user, parent, false);
            return new MessageViewHolder(view);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_friend, parent, false);
        return new MessageViewHolder(view);

//        View view = LayoutInflater.from(mContext).inflate(R.layout.messlayout, parent, false);
//        rowmessuser = view.findViewById(R.id.rowmessuser);
//        rowmesscurr = view.findViewById(R.id.rowmesscurr);
//
//
//
////        if (viewType == MSG_TYPE_RIGHT){
////            rowmessuser.setVisibility(View.GONE);
//////            rowmesscurr.setVisibility(View.VISIBLE);
//////            rowmessuser.setVisibility(View.INVISIBLE);
////        }
////        else{
////            rowmesscurr.setVisibility(View.GONE);
//////            rowmesscurr.setVisibility(View.INVISIBLE);
//////            rowmessuser.setVisibility(View.VISIBLE);
////        }
////        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_friend, parent, false);
////        return new MessageViewHolder(view);
//        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        final MessageMember mess = mUsers.get(position);
//        if (mUsers.get(position).getSenderuid().equals(firebaseUser.getUid())){
//            rowmessuser.setVisibility(View.GONE);
//            rowmesscurr.setVisibility(View.VISIBLE);
//        }else {
//            rowmesscurr.setVisibility(View.GONE);
//            rowmessuser.setVisibility(View.VISIBLE);
//        }


        String msg = mess.getMessage();
        String type = mess.getType();

        holder.message.setText(msg);
        if(getItemViewType(position) == 0){
            Glide.with(mContext).load(urlfr).into(holder.imageView3);
        }
        Log.d("URLUSER", "onBindViewHolder: " + urlfr);
//        Glide.with(mContext).load(urlfr).into(holder.imageView3);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        CircleImageView imageView2, imageView3;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);


            message = itemView.findViewById(R.id.textContent);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imguser);

        }

    }
    @Override
    public int getItemViewType(int position) {

        if (mUsers.get(position).getSenderuid().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
    private void userInfo(){

    }
}
//    public void Setmessage (Application application, String message, String time, String date, String type,
//                            String senderuid, String receiveruid){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String currentUid = user.getUid();
//
//        if(currentUid.equals(senderuid)){
//            layoutreceiver.setVisibility(View.GONE);
//            senddertv.setText(message);
//        }
//        else if (currentUid.equals(receiveruid)){
//            layoutsend.setVisibility(View.GONE);
//            receivertv.setText(message);
//        }
//    }
