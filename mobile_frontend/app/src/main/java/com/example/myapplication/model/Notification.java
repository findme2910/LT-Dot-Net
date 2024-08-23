package com.example.myapplication.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

// so sánh các thông báo dựa trên thời gian để tí nữa sort
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification{

    private String content;
    private String avatar;
    private String name;
    private Date createdAt;
    private boolean active;
    private int postId;


}
