package com.example.jwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "user_no")
    private int userNo;

    @Column(name = "user_id")
    @Setter
    private String userId;

    @Column(name = "user_pw")
    @Setter
    private String userPw;

    @Column(name = "name")
    @Setter
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "reg_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp regDate;

    @Column(name = "upd_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updDate;

    @Column(name = "enabled")
    private int enabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userNo")
    private final List<UserAuthEntity> userAuthEntityList = new ArrayList<>();

    @Builder
    public UserEntity(int userNo, String userId, String userPw, String name, String email, Timestamp regDate, Timestamp updDate, int enabled) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPw = userPw;
        this.name = name;
        this.email = email;
        this.regDate = regDate;
        this.updDate = updDate;
        this.enabled = enabled;
    }

}
