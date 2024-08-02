package com.example.myapplication.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.convert.DateConvert;
import com.example.myapplication.convert.DateConvertProfile;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.convert.ImageConvertProfile;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.api.Profile.ProfileManager;
import com.example.myapplication.network.model.dto.FriendViewDTO;
import com.example.myapplication.network.model.dto.PostViewDTO;
import com.example.myapplication.network.model.dto.ProfileDTO;
import com.example.myapplication.ui.adapters.FriendsListAdapter;
import com.example.myapplication.ui.adapters.PostProfileAdapter;
import com.example.myapplication.ui.adapters.ProfileAdapter;
import com.example.myapplication.ui.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;


public class FriendsListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewAllFriendList;
    private FriendsListAdapter friendsListAdapter;
    private List<FriendViewDTO> friends;
    private ImageView backButton;
    private int loggedUserId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        backButton = findViewById(R.id.back_arrow);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // Nhận loggedUserId từ Intent
        loggedUserId = getIntent().getIntExtra("LOGGED_USER_ID", -1);

        friends = new ArrayList<>();
        recyclerViewAllFriendList = findViewById(R.id.list_add_friend);
        recyclerViewAllFriendList.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    protected void onStart() {
        super.onStart();
        ProfileManager profileManager = new ProfileManager();

        // Nhận ID người dùng từ Intent
        int userId = getIntent().getIntExtra("USER_ID", -1);
            profileManager.getProfileForUser(userId, new HandleListener<ProfileDTO>() {
                @Override
                public void onSuccess(ProfileDTO profileDTO) {
                    friends = profileDTO.getFriends();
                    friendsListAdapter = new FriendsListAdapter(getApplicationContext(), friends);
                    recyclerViewAllFriendList.setAdapter(friendsListAdapter);

                    friendsListAdapter.setFriendListItemClickListener(new FriendsListAdapter.FriendListItemClickListener() {
                        @Override
                        public void onProfileClicked(int userId) {
                            Intent intent = new Intent(FriendsListActivity.this, ProfileActivity.class);
                            intent.putExtra("USER_ID", userId);
                            intent.putExtra("LOGGED_USER_ID", loggedUserId);
                            startActivity(intent);

                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Xử lý khi có lỗi xảy ra
                }
            });

//        }

    }
}
