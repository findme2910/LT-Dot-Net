package com.example.myapplication.network.api.Post;

import com.example.myapplication.network.model.dto.AddPostDTO;
import com.example.myapplication.network.model.dto.LikeDTO;
import com.example.myapplication.network.model.dto.PostViewDTO;
import com.example.myapplication.network.model.dto.ProfileDTO;
import com.example.myapplication.network.model.dto.ResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface PostApi {
    @GET("/post")
    public Call<List<PostViewDTO>> getPosts(@Header("Authorization") String token);

    @GET("/post/get/{id}")
    public Call<List<PostViewDTO>> getSpecificPost(@Header("Authorization") String token, @Path("id") int userId);

    @POST("/post")
    public Call<ResponseDTO> createPost(@Body AddPostDTO dto, @Header("Authorization") String token);

    @POST("/post/like")
    public Call<ResponseDTO> like(@Body LikeDTO dto, @Header("Authorization") String token);
}
