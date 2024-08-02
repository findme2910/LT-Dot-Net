package com.example.myapplication.network.api;

import com.example.myapplication.network.api.Profile.ProfileApi;
import com.example.myapplication.network.model.dto.LoginRequest;
import com.example.myapplication.network.model.dto.ProfileDTO;
import com.example.myapplication.network.model.dto.ResponseDTO;
import com.example.myapplication.network.model.dto.UserInformationDTO;
import com.example.myapplication.network.model.instance.JwtTokenManager;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginManager {
    public void loginUser(String username, String password, final OnLoginListener listener) {
        ApiInterface apiService = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<ResponseDTO> call = apiService.login(loginRequest);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    String token = response.body().getResponse();
                    if (listener != null) {
                        listener.onLoginSuccess(token);
                    }
                } else {
                    if (listener != null) {
                        String errorMessage = "Login failed";
                        if (response.errorBody() != null) {
                            try {
                                errorMessage = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onLoginFailure(errorMessage);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                if (listener != null) {
                    listener.onLoginFailure("Network error: " + t.getMessage());
                }
            }
        });
    }



    public interface OnLoginListener {
        void onLoginSuccess(String token);
        void onLoginFailure(String errorMessage);
    }
}
