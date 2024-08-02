package com.example.myapplication.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import com.example.myapplication.ui.activities.ProfileActivity;
import com.example.myapplication.ui.activities.ProfileEditActivity;
import com.example.myapplication.ui.activities.FriendsListActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private ActivityResultLauncher<Intent> profileEditLauncher;
    private int selectedUserId; // Biến để lưu ID người dùng đã chọn
    int loggedUserId;// Biến để lưu ID người dùng đăng nhập
    private int userId;
    private RecyclerView recyclerViewFriendList, recyclerViewPost;
    private ProfileAdapter profileAdapter;
    private PostProfileAdapter postProfileAdapter;

    private PostAdapter postAdapter;
    private List<FriendViewDTO> friends;

    private TextView countFriend;
    private TextView profileName, profileBirthday;

    private ImageView userAvatar;
    private Button editProfileButton, friendsListButton;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Liên kết với các TextView trong giao diện
        profileName = view.findViewById(R.id.profile_name);
        profileBirthday = view.findViewById(R.id.profile_birthDay);
        userAvatar = view.findViewById(R.id.profile_photo);

        countFriend = view.findViewById(R.id.numberOfFriends);
        friends = new ArrayList<>();

//        RecyclerView friend list
        recyclerViewFriendList = view.findViewById(R.id.profile_friend_list);
        recyclerViewFriendList.setLayoutManager(new GridLayoutManager(view.getContext(), 3));

//        RecyclerView post
        recyclerViewPost = view.findViewById(R.id.post_list);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(view.getContext()));


        editProfileButton = view.findViewById((R.id.edit_profile_button));
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
        loggedUserId = getActivity().getIntent().getIntExtra("LOGGED_USER_ID", -1);
        // Nhận userId từ Intent FriendListActivity
         userId = getActivity().getIntent().getIntExtra("USER_ID", -1);


        // Trong onClickListener của nút chỉnh sửa
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(editProfileButton.getText() + "----------------------------------------");
                if (editProfileButton.getText().toString().equalsIgnoreCase("Chỉnh sửa trang cá nhân")) {
                    Intent i = new Intent(getActivity(), ProfileEditActivity.class);
                    profileEditLauncher.launch(i); // Bắt đầu ProfileEditActivity
                } else if (editProfileButton.getText().toString().equalsIgnoreCase("Đã là bạn bè")){
                    editProfileButton.setText("Đã là bạn bè");
                    editProfileButton.setEnabled(false);
                } else {
                    System.out.println("userID:-------------------------------------- " + userId);
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



        friendsListButton = view.findViewById((R.id.all_friends_button));

        friendsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FriendsListActivity.class);
                i.putExtra("USER_ID", selectedUserId); // Truyền ID người dùng đã chọn vào Intent
                i.putExtra("LOGGED_USER_ID", loggedUserId);
                startActivity(i);
            }
        });

        loadUserProfile(loggedUserId, view);


//        if (loggedUserId != -1 && userId == -1) {
//            loadUserProfile(loggedUserId, view);
//        } else {
//            loadUserProfileForUser(userId, loggedUserId, view);
//        }
        return view;
    }


    private void loadUserProfile(int loggedUserId, View view) {
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


                profileAdapter = new ProfileAdapter(view.getContext(), firstSixFriends);
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
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("USER_ID", userId);
                        intent.putExtra("LOGGED_USER_ID", loggedUserId);
                        startActivity(intent);
//                        loadUserProfile(userId);
                    }
                });

                loadUserPosts(loggedUserId, view);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void loadUserPosts(int userId, View view) {
        PostManager postManager = new PostManager();
        List<PostViewDTO> result = new ArrayList<>();
        postManager.getSpecificPost(userId, new HandleListener<List<PostViewDTO>>() {
            @Override
            public void onSuccess(List<PostViewDTO> postList) {
                result.addAll(postList);
                postProfileAdapter = new PostProfileAdapter(view.getContext(), result);
                recyclerViewPost.setAdapter(postProfileAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {

            }

        });
    }


    // Phương thức kiểm tra nếu user được click là bạn bè
//    private boolean isFriend(int userId) {
//        for (FriendViewDTO friend : friends) {
//            if (friend.getUserId() == userId) {
//                return true;
//            }
//        }
//        return false;
//    }

//    private void loadUserProfileForUser(int userId, int loggedUserId, View view) {
//        selectedUserId = userId;
//
//        // Gọi API để lấy thông tin profile của user được chọn
//        ProfileManager profileManager = new ProfileManager();
//        profileManager.getProfileForUser(userId, new HandleListener<ProfileDTO>() {
//            @Override
//            public void onSuccess(ProfileDTO profileDTO) {
//                profileName.setText(profileDTO.getName());
//                profileBirthday.setText(DateConvertProfile.convertToString(profileDTO.getBirth()));
//                userAvatar.setImageBitmap(ImageConvertProfile.base64ToBitMap(profileDTO.getAvatar()));
//
//
//                friends = profileDTO.getFriends();
//
//                // Lấy 6 bạn đầu tiên
//                List<FriendViewDTO> firstSixFriends = friends.subList(0, Math.min(friends.size(), 6));
//
//
//                profileAdapter = new ProfileAdapter(view.getContext(), firstSixFriends);
//                recyclerViewFriendList.setAdapter(profileAdapter);
//
//                countFriend.setText(String.valueOf(friends.size()));
//
//
//                profileAdapter.setProfileClickListener(new ProfileAdapter.ProfileClickListener() {
//                    @Override
//                    public void onProfileClicked(int userId) {
//                        System.out.println("loggedUserID: " + loggedUserId);
//                        System.out.println("userID: " + userId);
//                        for(FriendViewDTO friendViewDTO : friends) {
//                            System.out.println("Friend: " + friendViewDTO);
//                        }
//                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
//                        intent.putExtra("USER_ID", userId);
//                        intent.putExtra("LOGGED_USER_ID", loggedUserId);
//                        startActivity(intent);
////                        loadUserProfile(userId);
//                    }
//                });
//                loadUserPosts(userId, view);
//
//
//
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//    }

}