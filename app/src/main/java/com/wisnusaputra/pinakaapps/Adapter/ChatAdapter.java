package com.wisnusaputra.pinakaapps.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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
import com.wisnusaputra.pinakaapps.Activity.MainActivity;
import com.wisnusaputra.pinakaapps.Fragment.ProfileFragment;
import com.wisnusaputra.pinakaapps.Model.User;
import com.wisnusaputra.pinakaapps.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<User> users, boolean isFragment){
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
        return new ChatAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

//        holder.btn_follow.setVisibility(View.VISIBLE);
//        isFollowing(user.getId(), holder.btn_follow);

        holder.username.setText(user.getUsername());
//        holder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);

//        if (user.getId().equals(firebaseUser.getUid())){
//            holder.btn_follow.setVisibility(View.GONE);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    Intent intent = new Intent(mContext, ChatPrivateActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView mess;
        public CircleImageView image_profile;
        public TextView lastviewd;

        public ChatViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.UserName);
            mess = itemView.findViewById(R.id.lastMessage);
            image_profile = itemView.findViewById(R.id.profileImage);
            lastviewd = itemView.findViewById(R.id.lastviewd);
        }

    }


}
