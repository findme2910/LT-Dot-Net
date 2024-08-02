package com.example.myapplication.network.model.instance;
public class JwtTokenManager {
    private static JwtTokenManager instance;
    private String token;

    private JwtTokenManager() {
        // Khởi tạo TokenManager
    }

    public static synchronized JwtTokenManager getInstance() {
        if (instance == null) {
            instance = new JwtTokenManager();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
