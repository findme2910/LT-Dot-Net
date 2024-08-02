package com.example.myapplication.network.api.Post;

import com.example.myapplication.network.api.ApiClient;
import com.example.myapplication.network.api.Friend.FriendApi;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.model.dto.*;
import com.example.myapplication.network.model.instance.JwtTokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class PostManager {
    PostApi postApi = ApiClient.getRetrofitInstance().create(PostApi.class);

    public void getPosts(HandleListener<List<PostViewDTO>> callback) {
        String token = JwtTokenManager.getInstance().getToken();
        Call<List<PostViewDTO>> call = postApi.getPosts("Bearer " + token);
        call.enqueue(new Callback<List<PostViewDTO>>() {
            @Override
            public void onResponse(Call<List<PostViewDTO>> call, Response<List<PostViewDTO>> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<PostViewDTO>> call, Throwable t) {
                callback.onFailure("Load Failure");
            }
        });
    }

    public void getSpecificPost(int userId, HandleListener<List<PostViewDTO>> callback) {
        String token = JwtTokenManager.getInstance().getToken();
        Call<List<PostViewDTO>> call = postApi.getSpecificPost("Bearer " + token, userId);
        call.enqueue(new Callback<List<PostViewDTO>>() {
            @Override
            public void onResponse(Call<List<PostViewDTO>> call, Response<List<PostViewDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to get specific post");
                }
            }

            @Override
            public void onFailure(Call<List<PostViewDTO>> call, Throwable t) {
                callback.onFailure("Load Failure");
            }
        });
    }

    public void like(LikeDTO dto, HandleListener<String> callback) {
        String token = JwtTokenManager.getInstance().getToken();
        Call<ResponseDTO> call = postApi.like(dto, "Bearer " + token);
        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                callback.onSuccess("Success");
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                callback.onFailure("Failure");
            }
        });
    }
    public void createPost(AddPostDTO dto, HandleListener<String> callback) {
        String token = JwtTokenManager.getInstance().getToken();
        Call<ResponseDTO> call = postApi.createPost(dto, "Bearer " + token);
        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                callback.onSuccess("Success");
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                callback.onFailure("Failure");
            }
        });
    }
}
