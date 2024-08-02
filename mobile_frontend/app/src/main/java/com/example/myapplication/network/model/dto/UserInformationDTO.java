package com.example.myapplication.network.model.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationDTO {
    private int userId;
    private String avatar;
    private String name;
}
