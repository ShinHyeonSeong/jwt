package com.example.jwt.domain;

import lombok.Data;


@Data
public class AuthenticationRequest {

    private String username;
    private String password;
}
