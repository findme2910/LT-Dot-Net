package com.example.myapplication.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.convert.DateConvert;
import com.example.myapplication.convert.DateConvertProfile;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.convert.ImageConvertProfile;
import com.example.myapplication.model.Post;
import com.example.myapplication.network.api.Friend.FriendAddManager;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.api.Profile.ProfileManager;
import com.example.myapplication.network.model.dto.FriendViewDTO;
import com.example.myapplication.network.model.dto.PostViewDTO;
import com.example.myapplication.network.model.dto.ProfileDTO;
import com.example.myapplication.network.model.instance.JwtTokenManager;
import com.example.myapplication.ui.adapters.PostAdapter;
import com.example.myapplication.ui.adapters.PostProfileAdapter;
import com.example.myapplication.ui.adapters.ProfileAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> profileEditLauncher;
    private int selectedUserId; // Biến để lưu ID người dùng đã chọn
    int loggedUserId;// Biến để lưu ID người dùng đăng nhập
    private RecyclerView recyclerViewFriendList, recyclerViewPost;
    private ProfileAdapter profileAdapter;
    private PostProfileAdapter postProfileAdapter;

    private PostAdapter postAdapter;
    private List<FriendViewDTO> friends;

    private TextView countFriend;
    private TextView profileName, profileBirthday;

    private ImageView userAvatar;
    private Button editProfileButton, friendsListButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Liên kết với các TextView trong giao diện
        profileName = findViewById(R.id.profile_name);
        profileBirthday = findViewById(R.id.profile_birthDay);
        userAvatar = findViewById(R.id.profile_photo);

        countFriend = findViewById(R.id.numberOfFriends);
        friends = new ArrayList<>();

//        RecyclerView friend list
        recyclerViewFriendList = findViewById(R.id.profile_friend_list);
        recyclerViewFriendList.setLayoutManager(new GridLayoutManager(this, 3));

//        RecyclerView post
        recyclerViewPost = findViewById(R.id.post_list);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(this));


        editProfileButton = findViewById((R.id.edit_profile_button));
        // Khai báo biến cho ActivityResultLauncher
        // Dùng để cập nhật avatar ở trang profile khi nhấn lưu
        profileEditLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Xử lý kết quả ở đây
                        Intent data = result.getData();
                        if (data != null) {
                            String newAvatar = data.getStringExtra("NEW_AVATAR");
                            if (newAvatar != null && !newAvatar.isEmpty()) {
                                userAvatar.setImageBitmap(ImageConvertProfile.base64ToBitMap(newAvatar));
                            }
                        }
                    }
                }
        );

        // Nhận userId từ Intent MainActivity
        loggedUserId = getIntent().getIntExtra("LOGGED_USER_ID", -1);

        // Nhận userId từ Intent FriendListActivity
        int userId = getIntent().getIntExtra("USER_ID", -1);


        // Trong onClickListener của nút chỉnh sửa
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editProfileButton.getText().toString().equalsIgnoreCase("Chỉnh sửa trang cá nhân")) {
                    Intent i = new Intent(getApplicationContext(), ProfileEditActivity.class);
                    profileEditLauncher.launch(i); // Bắt đầu ProfileEditActivity
                } else if (editProfileButton.getText().toString().equalsIgnoreCase("Đã là bạn bè")){
                    editProfileButton.setText("Đã là bạn bè");
                    editProfileButton.setEnabled(false);
                } else {
                    editProfileButton.setEnabled(true);
                    FriendAddManager friendAddManager = new FriendAddManager();
                    friendAddManager.requestAddFriend(userId, new HandleListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            editProfileButton.setText("Đã gửi lời mời");
                            editProfileButton.setEnabled(false);
                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });
                }
            }
        });


        friendsListButton = findViewById((R.id.all_friends_button));

        friendsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, FriendsListActivity.class);
                i.putExtra("USER_ID", selectedUserId); // Truyền ID người dùng đã chọn vào Intent
                i.putExtra("LOGGED_USER_ID", loggedUserId);
                startActivity(i);
            }
        });


        if (loggedUserId != -1 && userId == -1) {
            loadUserProfile(loggedUserId);
        } else {
            loadUserProfileForUser(userId, loggedUserId);

        }
    }


    private void loadUserProfile(int loggedUserId) {
        super.onStart();
        ProfileManager profileManager = new ProfileManager();
        profileManager.getProfile(new HandleListener<ProfileDTO>() {
            @Override
            public void onSuccess(ProfileDTO profileDTO) {
                profileName.setText(profileDTO.getName());
                profileBirthday.setText(DateConvertProfile.convertToString(profileDTO.getBirth()));
                userAvatar.setImageBitmap(ImageConvertProfile.base64ToBitMap(profileDTO.getAvatar()));

                friends = profileDTO.getFriends();
                selectedUserId = profileDTO.getUserId();

//                Lấy 6 bạn đầu tiên
                List<FriendViewDTO> firstSixFriends = friends.subList(0, Math.min(friends.size(), 6));


                profileAdapter = new ProfileAdapter(getApplicationContext(), firstSixFriends);
                recyclerViewFriendList.setAdapter(profileAdapter);

                countFriend.setText(String.valueOf(friends.size()));

                // Thiết lập ProfileClickListener cho profileAdapter
                profileAdapter.setProfileClickListener(new ProfileAdapter.ProfileClickListener() {
                    @Override
                    public void onProfileClicked(int userId) {
                        System.out.println("loggedUserID: " + loggedUserId);
                        System.out.println("userID: " + userId);
                        for(FriendViewDTO friendViewDTO : friends) {
                            System.out.println("Friend: " + friendViewDTO);
                        }
                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        intent.putExtra("USER_ID", userId);
                        intent.putExtra("LOGGED_USER_ID", loggedUserId);
                        startActivity(intent);
//                        loadUserProfile(userId);
                    }
                });

                loadUserPosts(loggedUserId);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void loadUserPosts(int userId) {
        PostManager postManager = new PostManager();
        List<PostViewDTO> result = new ArrayList<>();
        postManager.getSpecificPost(userId, new HandleListener<List<PostViewDTO>>() {
            @Override
            public void onSuccess(List<PostViewDTO> postList) {
                result.addAll(postList);
                postProfileAdapter = new PostProfileAdapter(getApplicationContext(), result);
                recyclerViewPost.setAdapter(postProfileAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {

            }

        });
    }


    // Phương thức kiểm tra nếu user được click là bạn bè
    private boolean isFriend(int userId) {
        for (FriendViewDTO friend : friends) {
            if (friend.getUserId() == userId) {
                return true;
            }
        }
        return false;
    }

    private void loadUserProfileForUser(int userId, int loggedUserId) {
        selectedUserId = userId;

        // Gọi API để lấy thông tin profile của user được chọn
        ProfileManager profileManager = new ProfileManager();
        profileManager.getProfileForUser(userId, new HandleListener<ProfileDTO>() {
            @Override
            public void onSuccess(ProfileDTO profileDTO) {
                profileName.setText(profileDTO.getName());
                profileBirthday.setText(DateConvertProfile.convertToString(profileDTO.getBirth()));
                userAvatar.setImageBitmap(ImageConvertProfile.base64ToBitMap(profileDTO.getAvatar()));


                friends = profileDTO.getFriends();

                // Lấy 6 bạn đầu tiên
                List<FriendViewDTO> firstSixFriends = friends.subList(0, Math.min(friends.size(), 6));


                profileAdapter = new ProfileAdapter(getApplicationContext(), firstSixFriends);
                recyclerViewFriendList.setAdapter(profileAdapter);

                countFriend.setText(String.valueOf(friends.size()));

                // Kiểm tra nếu user được click là bạn bè
                if (userId == loggedUserId) {
                    editProfileButton.setText("Chỉnh sửa trang cá nhân");
                    editProfileButton.setEnabled(true);
                } else if (isFriend(loggedUserId)) {
                    editProfileButton.setText("Đã là bạn bè");
                    editProfileButton.setEnabled(false);
                } else {
                    editProfileButton.setText("Kết bạn");
                    editProfileButton.setEnabled(true);
                }

                profileAdapter.setProfileClickListener(new ProfileAdapter.ProfileClickListener() {
                    @Override
                    public void onProfileClicked(int userId) {
                        System.out.println("loggedUserID: " + loggedUserId);
                        System.out.println("userID: " + userId);
                        for(FriendViewDTO friendViewDTO : friends) {
                            System.out.println("Friend: " + friendViewDTO);
                        }
                        // Kiểm tra nếu user được click là bạn bè
                        if (userId == loggedUserId) {
                            editProfileButton.setText("Chỉnh sửa trang cá nhân");
                            editProfileButton.setEnabled(true);
                        } else if (isFriend(loggedUserId)) {
                            editProfileButton.setText("Đã là bạn bè");
                            editProfileButton.setEnabled(false);
                        } else {
                            editProfileButton.setText("Kết bạn");
                            editProfileButton.setEnabled(true);
                        }
                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        intent.putExtra("USER_ID", userId);
                        intent.putExtra("LOGGED_USER_ID", loggedUserId);
                        startActivity(intent);
                    }
                });
                loadUserPosts(userId);



            }

            @Override
            public void onFailure(String errorMessage) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

}