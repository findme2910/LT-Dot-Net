package com.example.myapplication.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.convert.ImageConvert;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.Post.PostManager;
import com.example.myapplication.network.model.dto.AddPostDTO;
import com.example.myapplication.ui.fragment.HomeFragment;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

public class CreatePostActivity extends AppCompatActivity {
    private EditText content;
    private ImageView img;
    private ImageView avartarImageView;
    private TextView userNameTextView;
    private Button add, cancel;
    String userName;
    String avatarUrl;
    private boolean selectedImage = false;
    private PostManager postManager = new PostManager();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        content = findViewById(R.id.post_add_content);
        img = findViewById(R.id.post_add_image);

        avartarImageView = findViewById(R.id.post_user_avatar);
        userNameTextView = findViewById(R.id.post_user_name);
        add = findViewById(R.id.post_add_button);
        cancel = findViewById(R.id.post_cancel_button);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("username", "Default User");
        avatarUrl = sharedPreferences.getString("avatarUrl", "");


        userNameTextView.setText(userName);
        avartarImageView.setImageBitmap(ImageConvert.base64ToBitMap(avatarUrl));

        add.setVisibility(View.INVISIBLE);
        img.setOnClickListener(n -> {
            openImagePicker();
        });
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activeCreate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        add.setOnClickListener(n -> {
            Drawable drawable = img.getDrawable();
            Bitmap  bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();


            String encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            postManager.createPost(AddPostDTO.builder().image(encodedImage).content(content.getText().toString()).build(), new HandleListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getApplicationContext(), "Tạo Post Thành Công", Toast.LENGTH_LONG).show();
                    //tạo thành công sẽ chu3yển tới homeFragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new HomeFragment())
                            .addToBackStack(null)
                            .commit();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getApplicationContext(), "Tạo Post Thất bại", Toast.LENGTH_LONG).show();
                }
            });
        });
        cancel.setOnClickListener(n -> {
            finish();
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                // Xử lí khi chọn ảnh từ Gallery
                Uri selectedImageUri = data.getData();
                // Thực hiện thay đổi ảnh trên ImageView
                selectedImage = true;
                activeCreate();
                img.setImageURI(selectedImageUri);
            }
        }
    }

    private void activeCreate() {
        if (selectedImage && !content.getText().toString().isEmpty()) {
            add.setVisibility(View.VISIBLE);
        }
    }
}