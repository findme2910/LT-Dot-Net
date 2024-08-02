package com.example.myapplication.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.convert.ImageConvertProfile;
import com.example.myapplication.network.model.dto.FriendViewDTO;
import com.example.myapplication.ui.activities.ProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    @Setter
    private ProfileClickListener profileClickListener;
    private Context mContext;
    private List<FriendViewDTO> friends;

    public ProfileAdapter(Context context, List<FriendViewDTO> friendList) {
        mContext = context;
        friends = friendList;
    }

    public interface ProfileClickListener {
        void onProfileClicked(int userId);
    }


    @NonNull
    @NotNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.profile_friend_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        FriendViewDTO friend = friends.get(position);
        holder.nameTextView.setText(friend.getName());

//         Hiển thị hình ảnh trong ImageView
        ImageView imageView = holder.avatarImageView;
        imageView.setImageBitmap(ImageConvertProfile.base64ToBitMap(friend.getAvatar()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileClickListener != null) {
                    profileClickListener.onProfileClicked(friend.getUserId());
                }
            }


        });

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setData(List<FriendViewDTO> newData) {
        friends.clear();
        friends.addAll(newData);
    }


    @Setter
    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarImageView;
        public TextView nameTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.profile_friend_item_avatar);
            nameTextView = itemView.findViewById(R.id.profile_friend_item_name);
        }
    }



}
