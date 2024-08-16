package com.example.myapplication.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.myapplication.R;
import com.example.myapplication.model.Post;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Home.HomeManager;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.model.dto.HomeViewDTO;
import com.example.myapplication.network.model.dto.PostViewDTO;
import com.example.myapplication.ui.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class HomeActivity extends FragmentActivity {
    private ImageButton home, friend, noti, logout;
    private TextView numberAddFriend, numberOfNoti;
    private RecyclerView postsRecyclerView;

    private List<Post> posts;
    private PostAdapter postAdapter;

    private SwipeRefreshLayout homeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        home = findViewById(R.id.home_button);
        friend = findViewById(R.id.addFriend_button);
        noti = findViewById(R.id.notification_button);
        logout = findViewById(R.id.logout_button);

        numberAddFriend = findViewById(R.id.badge_addfriend);
        numberOfNoti = findViewById(R.id.badge_noti);

        postsRecyclerView = findViewById(R.id.list_post);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        home = findViewById(R.id.home_button);
        noti = findViewById(R.id.notification_button);
        friend = findViewById(R.id.addFriend_button);
        logout = findViewById(R.id.logout_button);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddFriendActivity.class);
                startActivity(i);
            }
        });
        noti.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivity(i);
        });
        homeRefresh = findViewById(R.id.swipe_refresh_home);



        homeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ApiCallTask().execute();
            }
        });
        posts = new ArrayList<>();




        HomeManager homeManager = new HomeManager();
        homeManager.getHomeView(new HandleListener<HomeViewDTO>() {
            @Override
            public void onSuccess(HomeViewDTO homeViewDTO) {
                if(homeViewDTO.getNumberNoti() == 0){
                    numberOfNoti.setText("");
                }
                else{
                    numberOfNoti.setText(homeViewDTO.getNumberNoti()+"");
                }
                if(homeViewDTO.getNumberAddFriend() == 0){
                    numberAddFriend.setText("");
                }
                else{
                    numberAddFriend.setText(homeViewDTO.getNumberAddFriend()+"");
                }

            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        new ApiCallTask().execute();
    }

    private class ApiCallTask extends AsyncTask<Void, Void, List<PostViewDTO>> {
        private final Semaphore semaphore = new Semaphore(0);

        @Override
        protected List<PostViewDTO> doInBackground(Void... voids) {
            PostManager postManager = new PostManager();
            List<PostViewDTO> result = new ArrayList<>();
            postManager.getPosts(new HandleListener<List<PostViewDTO>>() {
                @Override
                public void onSuccess(List<PostViewDTO> postViewDTOS) {
                    result.addAll(postViewDTOS);
                    semaphore.release();
                }

                @Override
                public void onFailure(String errorMessage) {
                    System.out.println(errorMessage);
                    semaphore.release();
                }
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<PostViewDTO> result) {
            super.onPostExecute(result);
            posts.clear();
            result.forEach(n -> {
                posts.add(Post.builder()
                        .postId(n.getPostId())
                        .userId(n.getUserId())
                        .img(n.getImage())
                        .name(n.getName())
                        .content(n.getContent())
                        .avatar(n.getAvatarUser())
                        .isLike(n.isLiked())
                        .numberOfComment(n.getNumberComment())
                        .numberOfLike(n.getNumberLike())
                        .createAt(n.getCreateAt())
                        .build());
            });
            postAdapter = new PostAdapter(posts, HomeActivity.this);
            postsRecyclerView.setAdapter(postAdapter);
            homeRefresh.setRefreshing(false);
        }
    }
}