package com.example.jwt.dto;

import com.example.jwt.entity.UserAuthEntity;
import com.example.jwt.entity.UserEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserAuthDto {
    private int authNo;
    private UserEntity userNo;
    private String auth;

    public static UserAuthDto toUserAuthDto(UserAuthEntity userAuthEntity) {
        return UserAuthDto.builder()
                .authNo(userAuthEntity.getAuthNo())
                .userNo(userAuthEntity.getUserNo())
                .auth(userAuthEntity.getAuth())
                .build();
    }

    public UserAuthEntity toUserAuthEntity() {
        return new UserAuthEntity.Builder()
                .authNo(authNo)
                .userNo(userNo)
                .auth(auth)
                .build();
    }
}
