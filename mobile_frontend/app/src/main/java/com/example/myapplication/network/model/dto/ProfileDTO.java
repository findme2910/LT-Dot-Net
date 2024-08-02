package com.example.myapplication.network.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private int userId;
    private String name;
    private long birth;
    private String avatar;
    private boolean own;
    private String phone;
    private List<FriendViewDTO> friends;
}
