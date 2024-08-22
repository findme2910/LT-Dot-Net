package com.example.myapplication.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.myapplication.R;
import com.example.myapplication.model.Post;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.model.dto.PostViewDTO;
import com.example.myapplication.ui.activities.HomeActivity;
import com.example.myapplication.ui.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


public class HomeFragment extends Fragment {

    private TextView numberAddFriend, numberOfNoti;
    private RecyclerView postsRecyclerView;

    private List<Post> posts;
    private PostAdapter postAdapter;

    private SwipeRefreshLayout homeRefresh;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postsRecyclerView = view.findViewById(R.id.list_post);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        homeRefresh = view.findViewById(R.id.swipe_refresh_home);

        homeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ApiCallTask().execute();
            }
        });
        posts = new ArrayList<>();

        new ApiCallTask().execute();
        return view;
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
                        .scope(n.getPostScope())
                        .numberOfComment(n.getNumberComment())
                        .numberOfLike(n.getNumberLike())
                        .createAt(n.getCreateAt())
                        .build());
            });
            postAdapter = new PostAdapter(posts, HomeFragment.this.getContext());
            postsRecyclerView.setAdapter(postAdapter);
            homeRefresh.setRefreshing(false);
        }
    }
}