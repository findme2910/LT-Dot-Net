package com.example.myapplication.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.convert.ImageConvertProfile;
import com.example.myapplication.network.model.dto.FriendViewDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {
    @Setter
    private FriendListItemClickListener friendListItemClickListener;
    private Context mContext;
    private List<FriendViewDTO> friends;

    public FriendsListAdapter(Context context, List<FriendViewDTO> friendList) {
        mContext = context;
        friends = friendList;
    }

    public interface FriendListItemClickListener {
        void onProfileClicked(int userId);
    }

    @NonNull
    @NotNull
    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.friends_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendViewDTO friend = friends.get(position);
        holder.nameTextView.setText(friend.getName());

//         Hiển thị hình ảnh trong ImageView
        ImageView imageView = holder.avatarImageView;
        imageView.setImageBitmap(ImageConvertProfile.base64ToBitMap(friend.getAvatar()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendListItemClickListener != null) {
                    friendListItemClickListener.onProfileClicked(friend.getUserId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    @Setter
    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatarImageView;
        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.friend_item_avatar);
            nameTextView = itemView.findViewById(R.id.friend_item_name);
        }
    }
}
