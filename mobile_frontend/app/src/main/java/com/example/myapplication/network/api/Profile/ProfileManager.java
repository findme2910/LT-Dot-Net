package com.example.myapplication.network.api.Profile;

import com.example.myapplication.network.api.ApiClient;
import com.example.myapplication.network.api.HandleListener;
import com.example.myapplication.network.model.dto.ProfileDTO;
import com.example.myapplication.network.model.dto.ResponseDTO;
import com.example.myapplication.network.model.dto.UpdateAvatarDTO;
import com.example.myapplication.network.model.dto.UserInformationDTO;
import com.example.myapplication.network.model.instance.JwtTokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileManager {
    public void getProfile(HandleListener<ProfileDTO> handleListener) {
        ProfileApi profileApi = ApiClient.getRetrofitInstance().create(ProfileApi.class);
        String token = JwtTokenManager.getInstance().getToken();
        Call<ProfileDTO> call = profileApi.getProfile("Bearer " + token);

        call.enqueue(new Callback<ProfileDTO>() {
            @Override
            public void onResponse(Call<ProfileDTO> call, Response<ProfileDTO> response) {
                handleListener.onSuccess(response.body());

            }

            @Override
            public void onFailure(Call<ProfileDTO> call, Throwable t) {
                handleListener.onFailure("load failure");
            }
        });
    }

    public void getProfileForUser(int userId, HandleListener<ProfileDTO> handleListener) {
        ProfileApi profileApi = ApiClient.getRetrofitInstance().create(ProfileApi.class);
        String token = JwtTokenManager.getInstance().getToken();
        Call<ProfileDTO> call = profileApi.getProfileForUser("Bearer " + token, userId);

        call.enqueue(new Callback<ProfileDTO>() {
            @Override
            public void onResponse(Call<ProfileDTO> call, Response<ProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleListener.onSuccess(response.body());
                } else {
                    handleListener.onFailure("Failed to load profile for user: " + userId);
                }
            }

            @Override
            public void onFailure(Call<ProfileDTO> call, Throwable t) {
                handleListener.onFailure("Failed to load profile for user: " + userId + " - " + t.getMessage());
            }
        });
    }

    public void updateAvatar(UpdateAvatarDTO updateAvatarDTO, HandleListener<String> handleListener) {
        ProfileApi profileApi = ApiClient.getRetrofitInstance().create(ProfileApi.class);
        String token = JwtTokenManager.getInstance().getToken();
        Call<ResponseDTO> call = profileApi.updateAvatar(updateAvatarDTO, "Bearer " + token);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    handleListener.onSuccess("Avatar updated successfully");
                } else {
                    handleListener.onFailure("Update avatar failure: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                handleListener.onFailure("Update avatar failure: " + t.getMessage());
            }
        });
    }

    public void getUserInfor(HandleListener<UserInformationDTO> handleListener) {
        ProfileApi profileApi = ApiClient.getRetrofitInstance().create(ProfileApi.class);
        String token = JwtTokenManager.getInstance().getToken();
        Call<UserInformationDTO> call = profileApi.getUserInfor("Bearer " + token);

        call.enqueue(new Callback<UserInformationDTO>() {
            @Override
            public void onResponse(Call<UserInformationDTO> call, Response<UserInformationDTO> response) {
                handleListener.onSuccess(response.body());

            }

            @Override
            public void onFailure(Call<UserInformationDTO> call, Throwable t) {
                handleListener.onFailure("load failure");
            }
        });
    }


}
