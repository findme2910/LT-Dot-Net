package com.example.myapplication.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.convert.DateConvert;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.model.Post;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.model.dto.LikeDTO;
import com.example.myapplication.ui.activities.CommentsActivity;

import com.example.myapplication.ui.fragment.MyBottomSheetFragment;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private PostManager postManager;

    private Context context;

    public PostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.postManager = new PostManager();
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.post_item, parent, false);
        PostAdapter.ViewHolder viewHolder = new PostAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", context.MODE_PRIVATE);
        int userid = sharedPreferences.getInt("iduser", 0);

        Post post = posts.get(position);
        holder.avatar.setImageBitmap(ImageConvert.base64ToBitMap(post.getAvatar()));
        holder.img.setImageBitmap(ImageConvert.base64ToBitMap(post.getImg()));
        holder.content.setText(post.getContent());
        holder.name.setText(post.getName());
        holder.comment.setText(post.getNumberOfComment() + " comments");
        //handle Comment
        holder.postComment.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("postID", post.getPostId());
            context.startActivity(intent);
        });
        if(post.getUserId() != userid){
            holder.mutiChoose.setVisibility(View.GONE);
        }
        changeScopeIcon(holder,post.getScope());
        holder.mutiChoose.setOnClickListener(n -> {
            MyBottomSheetFragment bottomSheet = new MyBottomSheetFragment();
            Bundle args = new Bundle();
            args.putInt("scope" , post.getScope());
            args.putInt("postId" , post.getPostId());
            bottomSheet.setArguments(args);
            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
            ((FragmentActivity) context).getSupportFragmentManager().setFragmentResultListener("requestKey",  ((FragmentActivity)context), new FragmentResultListener() {
                @Override
                public void onFragmentResult(String requestKey, Bundle result) {
                    if ("requestKey".equals(requestKey)) {
                        // Nhận kết quả từ Fragment
                        int newScope = result.getInt("newScope");
                        post.setScope(newScope);
                        changeScopeIcon(holder,newScope);
                    }
                }
            });
        });
        holder.like.setText(post.getNumberOfLike() + " likes");
        if (post.isLike()) {
            holder.likeButton.setImageResource(R.drawable.heart);
        } else {

            holder.likeButton.setImageResource(R.drawable.icon_heart);
        }
        holder.likeButton.setOnClickListener(n -> {
            changeLike(holder, post);
            postManager.like(LikeDTO.builder().postId(post.getPostId()).build(), new HandleListener<String>() {
                @Override
                public void onSuccess(String s) {
                }
                @Override
                public void onFailure(String errorMessage) {
                    System.out.println(errorMessage);
                    changeLike(holder, post);
                }
            });
        });
        holder.createAt.setText(DateConvert.convertToString(post.getCreateAt()));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private static void changeLike(ViewHolder holder, Post post) {
        if (post.isLike()) {
            post.setNumberOfLike(post.getNumberOfLike() - 1);
            holder.likeButton.setImageResource(R.drawable.icon_heart);
            post.setLike(false);
        } else {
            post.setNumberOfLike(post.getNumberOfLike() + 1);
            holder.likeButton.setImageResource(R.drawable.heart);
            post.setLike(true);
        }
        holder.like.setText(post.getNumberOfLike() + " likes");
    }
    private void changeScopeIcon(ViewHolder view, int scope){
        if (scope == 0) {
            view.scopeIcon.setImageResource(R.drawable.baseline_public_24);
        } else if (scope == 1) {
            view.scopeIcon.setImageResource(R.drawable.friend);
        } else {
            view.scopeIcon.setImageResource(R.drawable.baseline_lock_24);
        }
    }
    @Getter
    @Setter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView createAt;
        private TextView content;
        private ImageView img;
        private TextView like;
        private TextView comment;
        private ImageButton likeButton;
        private ImageButton postComment;
        private ImageView mutiChoose,scopeIcon;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.post_user_avatar);
            name = itemView.findViewById(R.id.post_user_name);
            createAt = itemView.findViewById(R.id.post_createAt);
            content = itemView.findViewById(R.id.post_content);
            img = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.post_number_like);
            comment = itemView.findViewById(R.id.post_number_comment);
            likeButton = itemView.findViewById(R.id.post_like_button);
            postComment = itemView.findViewById(R.id.post_comment);
            mutiChoose = itemView.findViewById(R.id.muti_setting);
            scopeIcon = itemView.findViewById(R.id.scope_icon);
        }
    }


}
