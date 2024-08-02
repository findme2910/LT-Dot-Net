package com.example.myapplication.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.convert.DateConvertProfile;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.convert.ImageConvertProfile;
import com.example.myapplication.network.api.ApiClient;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Profile.ProfileApi;
import com.example.myapplication.network.api.Profile.ProfileManager;
import com.example.myapplication.network.model.dto.FriendViewDTO;
import com.example.myapplication.network.model.dto.ProfileDTO;
import com.example.myapplication.network.model.dto.UpdateAvatarDTO;
import com.example.myapplication.network.model.instance.JwtTokenManager;
import com.example.myapplication.ui.adapters.ProfileAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ProfileEditActivity extends AppCompatActivity {
    private ImageView backButton;
    private EditText editName, editBirthday, editPhone;
    private ImageView userAvatar, image_load;
    private ProfileManager profileManager;
    private Button saveUpdate, chooseImage;
    private ActivityResultLauncher<String> mGetContent;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        backButton = findViewById(R.id.back_arrow);

        // Liên kết với các TextView trong giao diện
        editName = findViewById(R.id.edit_name);
        editBirthday = findViewById(R.id.edit_birthDay);
        userAvatar = findViewById(R.id.edit_image);
        saveUpdate = findViewById(R.id.saveUpdate);
        chooseImage = findViewById(R.id.chooseImage_button);
//        image_load = findViewById(R.id.image_load);

        profileManager = new ProfileManager();

        loadProfile();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    // Khởi tạo ActivityResultLauncher để lấy hình ảnh từ thư viện
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    userAvatar.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi chạy ActivityResultLauncher để lấy hình ảnh từ thư viện
                mGetContent.launch("image/*");
            }
        });

        saveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ảnh từ ImageView
                Bitmap avatarBitmap = ((BitmapDrawable)userAvatar.getDrawable()).getBitmap();

                // Chuyển đổi Bitmap thành chuỗi Base64
                String base64Avatar = ImageConvertProfile.bitMapToBase64(avatarBitmap);
                System.out.println(base64Avatar);

                UpdateAvatarDTO updateAvatarDTO = new UpdateAvatarDTO(base64Avatar);
                profileManager.updateAvatar(updateAvatarDTO, new HandleListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ProfileEditActivity.this, result, Toast.LENGTH_SHORT).show();
                        // Cập nhật hình ảnh trong ProfileActivity
                        Intent intent = new Intent();
                        intent.putExtra("NEW_AVATAR", base64Avatar);
                        setResult(RESULT_OK, intent);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProfileEditActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    protected void loadProfile() {
        super.onStart();
        ProfileManager profileManager = new ProfileManager();
        profileManager.getProfile(new HandleListener<ProfileDTO>() {
            @Override
            public void onSuccess(ProfileDTO profileDTO) {
                editName.setText(profileDTO.getName());
                editBirthday.setText(DateConvertProfile.convertToString(profileDTO.getBirth()));
                userAvatar.setImageBitmap(ImageConvertProfile.base64ToBitMap(profileDTO.getAvatar()));
            }
            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }


}
