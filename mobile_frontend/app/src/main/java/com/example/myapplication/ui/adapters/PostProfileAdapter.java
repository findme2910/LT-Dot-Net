package com.example.myapplication.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.convert.DateConvert;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.model.Post;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.model.dto.LikeDTO;
import com.example.myapplication.network.model.dto.PostViewDTO;

import java.util.List;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.PostViewHolder> {
    private Context context;
    private List<PostViewDTO> postList;
    private PostManager postManager;

    public PostProfileAdapter(Context context, List<PostViewDTO> postList) {
        this.context = context;
        this.postList = postList;
        this.postManager = new PostManager();
    }

    @NonNull
    @Override
    public PostProfileAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.post_item, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostViewDTO post = postList.get(position);
        holder.avatar.setImageBitmap(ImageConvert.base64ToBitMap(post.getAvatarUser()));
        holder.img.setImageBitmap(ImageConvert.base64ToBitMap(post.getImage()));
        holder.content.setText(post.getContent());
        holder.name.setText(post.getName());
        holder.comment.setText(post.getNumberComment()+" comments");
        holder.like.setText(post.getNumberLike()+" likes");
        if (post.isLiked()) {

            holder.likeButton.setImageResource(R.drawable.heart);
        } else {

            holder.likeButton.setImageResource(R.drawable.icon_heart);
        }
        holder.likeButton.setOnClickListener(n ->{
            changeLike(holder,post);
            postManager.like(LikeDTO.builder().postId(post.getPostId()).build(), new HandleListener<String>() {
                @Override
                public void onSuccess(String s) {
                }
                @Override
                public void onFailure(String errorMessage) {
                    System.out.println(errorMessage);
                    changeLike(holder,post);
                }
            });
        });
        holder.createAt.setText(DateConvert.convertToString(post.getCreateAt()));
        
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private static void changeLike(PostViewHolder holder , PostViewDTO post){
        if(post.isLiked()) {
            post.setNumberLike(post.getNumberLike() - 1);
            holder.likeButton.setImageResource(R.drawable.icon_heart);
            post.setLiked(false);
        }
        else{
            post.setNumberLike(post.getNumberLike() + 1);
            holder.likeButton.setImageResource(R.drawable.heart);
            post.setLiked(true);
        }
        holder.like.setText(post.getNumberLike()+" likes");
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView createAt;
        private TextView content;
        private ImageView img;
        private TextView like;
        private TextView comment;
        private ImageButton likeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.post_user_avatar);
            name = itemView.findViewById(R.id.post_user_name);
            createAt = itemView.findViewById(R.id.post_createAt);
            content = itemView.findViewById(R.id.post_content);
            img = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.post_number_like);
            comment = itemView.findViewById(R.id.post_number_comment);
            likeButton = itemView.findViewById(R.id.post_like_button);
        }
    }
}
