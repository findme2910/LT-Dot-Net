package com.example.myapplication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    private int postId;
    private int userId;
    private String name;
    private String avatar;
    private String img;
    private String content;
    private long createAt;
    private int numberOfComment;
    private int numberOfLike;
    private boolean isLike;
    private int scope;
}
