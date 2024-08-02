package com.example.myapplication.network.api.Profile;

import com.example.myapplication.network.model.dto.FriendRequest;
import com.example.myapplication.network.model.dto.ProfileDTO;
import com.example.myapplication.network.model.dto.ResponseDTO;
import com.example.myapplication.network.model.dto.UpdateAvatarDTO;
import com.example.myapplication.network.model.dto.UserInformationDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProfileApi {
    //    String token để biết user nào login
    @GET("/profile")
    Call<ProfileDTO> getProfile(@Header("Authorization") String token);

    @GET("/profile/{id}")
    Call<ProfileDTO> getProfileForUser(@Header("Authorization") String token, @Path("id") int userId);

    @POST("/profile/updateAvatar")
    Call<ResponseDTO> updateAvatar(@Body UpdateAvatarDTO updateAvatar  , @Header("Authorization") String token);

    @GET("/user")
    Call<UserInformationDTO> getUserInfor(@Header("Authorization") String token);

}
