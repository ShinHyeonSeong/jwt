package com.example.jwt.dto;

import com.example.jwt.entity.UserEntity;
import lombok.*;

import java.sql.Timestamp;
@Getter
@Setter
@Builder
public class UserDto {
    private int userNo;
    private String userId;
    private String userPw;
    private String name;
    private String email;
    private Timestamp regDate;
    private Timestamp updDate;
    private int enabled;

    public UserDto(){}

    @Builder
    public UserDto(int userNo, String userId, String userPw, String name, String email, Timestamp regDate, Timestamp updDate, int enabled) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPw = userPw;
        this.name = name;
        this.email = email;
        this.regDate = regDate;
        this.updDate = updDate;
        this.enabled = enabled;
    }

    public static UserDto toUserDto(UserEntity userEntity) {
        return UserDto.builder()
                .userNo(userEntity.getUserNo())
                .userId(userEntity.getUserId())
                .userPw(userEntity.getUserPw())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .regDate(userEntity.getRegDate())
                .updDate(userEntity.getUpdDate())
                .enabled(userEntity.getEnabled())
                .build();
    }

    public UserEntity toUserEntity() {
        return UserEntity.builder()
                .userNo(userNo)
                .userId(userId)
                .userPw(userPw)
                .name(name)
                .email(email)
                .regDate(regDate)
                .updDate(updDate)
                .enabled(enabled)
                .build();
    }


}
