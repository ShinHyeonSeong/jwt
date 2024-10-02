package com.example.jwt.constants;

// JWT 관련 상수 정의
public class JwtConstants {
    // 로그인 필터 경로
    public static final String AUTH_LOGIN_URL = "/login";

    // JWT 토큰을 담을 HTTP 요청 헤더 이름
    public static final String TOKEN_HEADER = "Authorization";

    // 헤더의 접두사, 관용적으로 Bearer 로 사용
    public static final String TOKEN_PREFIX = "Bearer ";    // 띄어쓰기 주의. 한 칸을 띄워야함.

    // 토큰 타입
    public static final String TOKEN_TYPE = "JWT";
}
