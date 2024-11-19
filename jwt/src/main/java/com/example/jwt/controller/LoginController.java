package com.example.jwt.controller;

import com.example.jwt.security.jwt.constants.JwtConstants;
import com.example.jwt.domain.AuthenticationRequest;
import com.example.jwt.prop.JwtProp;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private JwtProp jwtProp;    // 시크릿 키를 등록해놓은 Component 빈 클래스

    // login
    // userName
    // password
    // 매개변수는 유저 정보를 담을 DTO를 대체하는 형식의 클래스임.
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticationRequest request) {
        String userName = request.getUsername();
        String password = request.getPassword();
        log.info("userName = " + userName);
        log.info("password = " + password);

        // 사용자 권한
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");

        // 시크릿 키. 토큰의 시그니처로 실제 사용 시 바이트 형식으로 사용해야함
        byte[] signingKey = jwtProp.getSecretKey().getBytes();

        // 토큰 생성
        String jwt = Jwts.builder()

                // 시그니처 정보 (시크릿키, 암호화 알고리즘 종류)
                // 시크릿 키는 단순 바이트나 문자열 값은 지원되지 않게 바뀌었다. 따라서 Key 객체 타입으로 넣어주는 것이 바람직함.
                // HMAC (Keyed-Hashed Message Authentication Code) 인증을 위한 시크릿 키와 임의 길이의 메세지를 해시 함수(sha) 알고리즘을 사용해 생성한다.
                // 이는 시크릿 키가 달라질 경우 암호값 역시 실시간으로 변경된다.
                .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)              // 시크릿 키와 암호화 알고리즘

                // 헤더 정보
                .header()                                                              // 헤더 설정
                    .add("typ", JwtConstants.TOKEN_TYPE)                       // 토큰 타입. String 문자열로 넣어도 무방
                .and()
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24*5))    // 토큰 만료 시간. 5일로 설정함

                // Claim은 JWT의 body로써, JWT 생성자가 JWT를 받는 이들에게 제시하기 바라는 정보를 포함한다.
                // 사용자 지정 Claim
                .claim("uid", userName)                                             // PAYLOAD 유저 이름
                .claim("role", roles)                                               // PAYLOAD 권한
                .compact();                                                            // 토큰 압축 및 서명
        log.info("jwt : " + jwt);
        return new ResponseEntity<String>(jwt, HttpStatus.OK);
    }

    // 토큰 해석 컨트롤러
    @GetMapping("/user/info")
    public ResponseEntity<String> userInfo(@RequestHeader(name = "Authorization")String header) {
        log.info("Authorization : " + header);

        // Authorization : Bearer ${jwt} 형식에서 Bearer를 잘라야함.
        String jwt = header.replace(JwtConstants.TOKEN_PREFIX, "");    // SecurityConstants에서 static 상수로 지정한 값이 있음.

        // 시크릿 키 가져오기
        byte[] signingKey = jwtProp.getSecretKey().getBytes();

        // 토큰 디코딩
        Jws<Claims> parsedToken = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey))     // 시크릿 키를 사용해 검증
                .build()
                .parseSignedClaims(jwt);                        // jwt 토큰 내부 암호화된 데이터를 디코딩 해주는 작업

        // 복호화된 토큰의 내부 데이터. 미리 지정해둔 claim으로 접근 가능
        Claims claims = parsedToken.getPayload();               // 디코딩된 페이로드

        // 유저 정보 가져오기.
        String userName = parsedToken.getPayload().get("uid").toString();
        // String userName = claims.get("uid").toString();
        Object roles = claims.get("role");
        log.info(userName);
        log.info("roles : " + roles);

        return new ResponseEntity<String>(parsedToken.toString(), HttpStatus.OK); // 디코딩된 토큰을 반환
    }

}
