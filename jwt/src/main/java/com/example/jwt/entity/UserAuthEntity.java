package com.example.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_auth")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserAuthEntity {
    @Id
    @Column(name = "auth_no")
    private int authNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private UserEntity userNo;

    @Column(name = "auth")
    private String auth;

    static public class Builder {
        private int authNo;
        private UserEntity userNo;
        private String auth;

        public Builder authNo(int authNo) {
            this.authNo = authNo;
            return this;
        }

        public Builder userNo(UserEntity userNo) {
            this.userNo = userNo;
            return this;
        }

        public Builder auth(String auth) {
            this.auth = auth;
            return this;
        }

        public UserAuthEntity build() {
            return new UserAuthEntity(authNo, userNo, auth);
        }


    }

}
