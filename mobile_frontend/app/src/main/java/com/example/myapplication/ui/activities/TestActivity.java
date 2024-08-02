package com.example.myapplication.ui.activities;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.myapplication.R;
import com.example.myapplication.ui.fragment.CommentsFragment;
import com.example.myapplication.ui.fragment.AddFriendFragment;
import com.example.myapplication.ui.fragment.HomeFragment;
import com.example.myapplication.ui.fragment.ProfileFragment;
import com.example.myapplication.ui.fragment.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TestActivity extends AppCompatActivity {
    private LinearLayout createPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final Fragment homeFragment = new HomeFragment();

        //test
        final Fragment commentFragment = new CommentsFragment();
        final Fragment notificationFragment = new NotificationFragment();

        final Fragment friendFragment = new AddFriendFragment();
        final Fragment profileFragment = new ProfileFragment();

        createPost = findViewById(R.id.create_post);


        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        createPost.setOnClickListener(n -> {
            Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
            startActivity(intent);
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Xác định fragment tương ứng với mục được chọn
            if (item.getItemId() == R.id.navigation_home) {
                replaceFragment(homeFragment);
                createPost.setVisibility(View.VISIBLE);
            }

            if (item.getItemId() == R.id.navigation_notification) {
                replaceFragment(notificationFragment);
                createPost.setVisibility(View.GONE);
            }

            if (item.getItemId() == R.id.navigation_friends) {
                replaceFragment(friendFragment);
                createPost.setVisibility(View.GONE);
            }
            if (item.getItemId() == R.id.navigation_profile) {
                replaceFragment(profileFragment);
            }
            if (item.getItemId() == R.id.navigation_logout) {
                finish();
            }
            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}