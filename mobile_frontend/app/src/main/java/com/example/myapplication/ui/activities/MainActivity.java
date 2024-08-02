package com.example.myapplication.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.api.LoginManager;
import com.example.myapplication.network.api.Profile.ProfileManager;
import com.example.myapplication.network.model.dto.UserInformationDTO;
import com.example.myapplication.network.api.User.UserManager;
import com.example.myapplication.network.model.dto.UserInformationDTO;
import com.example.myapplication.network.model.instance.JwtTokenManager;

public class MainActivity extends AppCompatActivity {
    Button buttonLogin;
    EditText editTextUsername, editTextPassword;

    int userId;
    TextView tvSignUp;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);
        buttonLogin = findViewById(R.id.log_in_button);
        tvSignUp = findViewById(R.id.tv_sign_up);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (validateInput(username, password)) {
                    performLogin(username, password);
                }
            }
        });

        // Thêm sự kiện onClick cho tvSignUp
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            editTextUsername.setError("Phone is required");
            editTextUsername.requestFocus();
            return false;
        }

        if (!isValidPhoneNumber(username)) {
            editTextUsername.setError("Please enter a valid phone number");
            editTextUsername.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performLogin(String username, String password) {
        LoginManager loginManager = new LoginManager();
        loginManager.loginUser(username, password, new LoginManager.OnLoginListener() {
            @Override
            public void onLoginSuccess(String token) {

                // Lấy SharedPreferences
                JwtTokenManager.getInstance().setToken(token);

                        ProfileManager profileManager = new ProfileManager();
                        profileManager.getUserInfor(new HandleListener<UserInformationDTO>() {
                            @Override
                            public void onSuccess(UserInformationDTO userInformationDTO) {
                                userId = userInformationDTO.getUserId();
                                // Chuyển hướng sang TestActivity
        
                // Gọi API lấy ra thông tin người dùng
                UserManager.getProfileUser(new HandleListener<UserInformationDTO>() {
                    @Override
                    public void onSuccess(UserInformationDTO userInformationDTO) {
                        // Lưu thông tin người dùng vào sharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", userInformationDTO.getName());
                        editor.putString("avatarUrl", userInformationDTO.getAvatar());
                        editor.putInt("iduser", userInformationDTO.getUserId());
                        editor.apply();

                        // Chuyển sang homeActivity
                        Intent i = new Intent(getApplicationContext(), TestActivity.class);
                                i.putExtra("LOGGED_USER_ID", userId); // Đặt giá trị userId vào Intent
                                startActivity(i);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                System.out.println("Get id fail!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("UserInfo", "Error: " + errorMessage);
                    }
                });
            }

            @Override
            public void onLoginFailure(String errorMessage) {
                if (errorMessage.contains("Invalid email")) {
                    editTextUsername.setError("Invalid email");
                    editTextUsername.requestFocus();
                } else if (errorMessage.contains("Invalid password")) {
                    editTextPassword.setError("Invalid password");
                    editTextPassword.requestFocus();
                } else {
                    Toast.makeText(MainActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Regex pattern for phone number validation (Vietnam phone number example)
        String phonePattern = "^\\d{10,11}$"; // You can adjust the pattern to match your specific requirements
        return phoneNumber.matches(phonePattern);
    }
}


//0854705932