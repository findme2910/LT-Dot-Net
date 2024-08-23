package com.example.myapplication.ui.adapters.comment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.convert.DateConvert;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.model.Comment.Comment;
import com.example.myapplication.network.api.Comment.CommentManager;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.ui.fragment.CommentsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        int adapterPosition = holder.getBindingAdapterPosition();
        Comment comment = commentList.get(adapterPosition);
        holder.textViewUsername.setText(comment.getUserName());
        holder.textViewComment.setText(comment.getText());
        holder.textViewCreateAt.setText(DateConvert.convertToString(comment.getCreateAt().getTime()));
        ImageView imageView = holder.imageViewAvatar;
        imageView.setImageBitmap(ImageConvert.base64ToBitMap(comment.getAvatar()));

        // Handle reply click
        holder.textViewReply.setOnClickListener(v -> {
            holder.replyInputLayout.setVisibility(View.VISIBLE);
            holder.editTextReply.requestFocus();
        });

        // Handle reply input losing focus
        holder.editTextReply.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                holder.replyInputLayout.setVisibility(View.GONE);
            }
        });


        holder.buttonSendReply.setOnClickListener(v -> {
            String replyText = holder.editTextReply.getText().toString().trim();
            if (!replyText.isEmpty()) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                String currentUserName = sharedPreferences.getString("username", "Unknown User");
                String currentUserAvatar = sharedPreferences.getString("avatarUrl", "");
                // Gửi câu trả lời mới lên server
                CommentManager commentManager = new CommentManager();
                commentManager.addreplyComment(comment.getCommentId(), replyText, new HandleListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Calendar calendar = Calendar.getInstance();
                        // Thêm câu trả lời vào danh sách câu trả lời và cập nhật giao diện
                        Comment replyComment = new Comment(currentUserName, replyText, currentUserAvatar, calendar.getTime(), comment.getPostID(), new ArrayList<>());
                        comment.getReplies().add(replyComment);
                        notifyItemChanged(adapterPosition);
                        holder.editTextReply.setText(""); // Xóa văn bản sau khi gửi câu trả lời
                        holder.replyInputLayout.setVisibility(View.GONE); // Ẩn giao diện nhập câu trả lời
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Xử lý lỗi khi gửi câu trả lời
                        Toast.makeText(context, "Failed to add reply: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        // Display replies
        if (comment.getReplies().isEmpty()) {
            holder.recyclerViewReplies.setVisibility(View.GONE);
        } else {
            holder.recyclerViewReplies.setVisibility(View.VISIBLE);
            ReplyAdapter replyAdapter = new ReplyAdapter(context, comment.getReplies());
            holder.recyclerViewReplies.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerViewReplies.setAdapter(replyAdapter);
        }
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewComment;
        TextView textViewCreateAt;
        ImageView imageViewAvatar;
        TextView textViewReply;
        RecyclerView recyclerViewReplies;
        EditText editTextReply;
        Button buttonSendReply;
        View replyInputLayout;
        // thời gian sẽ set sau
        //TextView createAt;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            textViewCreateAt = itemView.findViewById(R.id.textViewCreateAt);
            recyclerViewReplies = itemView.findViewById(R.id.recyclerViewReplies);
            textViewReply = itemView.findViewById(R.id.textViewReply);
            editTextReply = itemView.findViewById(R.id.editTextReply);
            buttonSendReply = itemView.findViewById(R.id.buttonSendReply);
            replyInputLayout = itemView.findViewById(R.id.replyInputLayout);
        }
    }
}

