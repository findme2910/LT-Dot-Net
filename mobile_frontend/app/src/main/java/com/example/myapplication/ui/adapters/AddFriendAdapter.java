package com.example.myapplication.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.model.FriendRequestModel;
import com.example.myapplication.network.api.Friend.FriendAddManager;
import com.example.myapplication.network.api.HandleListener;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
// được sử dụng để hiển thị danh sách kết bạn đến người dùng
public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {
    private Context mContext;
    private List<FriendRequestModel> mFriendRequests;

    private FriendAddManager friendAddManager;

    public AddFriendAdapter(Context context, List<FriendRequestModel> friendRequests) {
        mContext = context;
        mFriendRequests = friendRequests;
        friendAddManager = new FriendAddManager();
    }

    @NonNull
    @NotNull
    @Override
    public AddFriendAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.add_friend_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }
    //thiết lập dữ liệu cho từng mục trong danh sách yêu cầu kết bạn khi mục đó xuất hiện len màn hình
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull AddFriendAdapter.ViewHolder holder, int position) {
        FriendRequestModel friendRequest = mFriendRequests.get(position);
        holder.nameTextView.setText(friendRequest.getName());
        holder.timeTextView.setText(friendRequest.getTime());

        // Hiển thị hình ảnh trong ImageView
        ImageView imageView = holder.avatarImageView;
        imageView.setImageBitmap(ImageConvert.base64ToBitMap(friendRequest.getAvatar()));


        // Sử lý sự kiện cho các nút
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendAddManager.acceptAddFriend(friendRequest.getUserId(), new HandleListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        holder.status.setVisibility(View.VISIBLE);
                        holder.acceptButton.setVisibility(View.GONE);
                        holder.deleteButton.setVisibility(View.GONE);
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                    }
                });
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendAddManager.rejectAddFriend(friendRequest.getUserId(), new HandleListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        holder.status.setText("Đã gỡ lời mời");
                        holder.status.setVisibility(View.VISIBLE);
                        holder.acceptButton.setVisibility(View.GONE);
                        holder.deleteButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriendRequests.size();
    }

    @Setter
    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatarImageView;
        public TextView nameTextView;
        public TextView timeTextView;
        public Button deleteButton;
        public Button acceptButton;
        public TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.add_friend_status);
            avatarImageView = itemView.findViewById(R.id.add_friend_avatar);
            nameTextView = itemView.findViewById(R.id.add_friend_name);
            timeTextView = itemView.findViewById(R.id.add_friend_time_request);
            deleteButton = itemView.findViewById(R.id.add_friend_delete);
            acceptButton = itemView.findViewById(R.id.add_friend_accept);
        }
    }
}
