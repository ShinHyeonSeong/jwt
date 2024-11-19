package com.example.jwt.security.jwt.provider;

import com.example.jwt.security.jwt.constants.JwtConstants;
import com.example.jwt.dto.CustomUser;
import com.example.jwt.dto.UserDto;
import com.example.jwt.prop.JwtProp;
import com.example.jwt.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 토큰 관련 기능을 제공해주는 클래스
 * 1. 토큰 생성
 * 2. 토큰 해석
 * 3. 토큰 유효성 검사
 */

@Slf4j
@Component
public class JwtTokenProvider {

    @Autowired
    private JwtProp jwtProp;
    @Autowired
    private UserService userService;

    // 토큰 생성
    public String createToken(int userNo, String userId, List<String> roles) {

        // JWT 토큰 생성
        String jwt = Jwts.builder()
                .signWith(getShaKey(), Jwts.SIG.HS512)          // 시그니처에 사용할 키와 알고리즘 설정
                .header().add("typ", JwtConstants.TOKEN_TYPE)                   // 헤더 설정(JWT)
                .and()
                .expiration(new Date(System.currentTimeMillis() + 864000000))       // 토큰 만료 시간 설정 (10일)
                .claim("uno", "" + userNo)                                      // 사용자 정보 클레임 설정
                .claim("uid", userId)
                .claim("rol", roles)
                .compact();

        log.info("jwt : " + jwt);
        return jwt;
    }

    // 토큰 해석
    public UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {
        if (authHeader == null || authHeader.length() == 0) {
            return null;
        }

        try {

            // 매개변수로 전달된 jwt 추출 ( Bearer + jwt ) > jwt
            String jwt = authHeader.replace(JwtConstants.TOKEN_PREFIX, "");

            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(getShaKey())
                    .build()
                    .parseSignedClaims(jwt);

            log.info("parsedToken : " + parsedToken);

            // 인증된 userNo
            String userNo = parsedToken.getPayload().get("uno").toString();
            int no = (userNo != null ? Integer.parseInt(userNo) : -1);
            log.info("userNo : " + no);

            // 인증된 userId
            String userId = parsedToken.getPayload().get("uid").toString();
            log.info("userNo : " + userId);

            // 인증된 roles
            Claims claims = parsedToken.getPayload();
            Object roles = claims.get("rol");
            log.info("roles : " + roles);

            if (no == -1) {
                return null;
            }

            UserDto userDto = new UserDto();
            userDto.setUserNo(no);
            userDto.setUserId(userId);

            // CustomUser 에서 사용할 수 있도록 SimpleGrantedAuthority 타입으로 권한 추출
            List<SimpleGrantedAuthority> authorities = ((List<?>)roles)
                    .stream()
                    .map(auth -> new SimpleGrantedAuthority((String)auth))
                    .collect(Collectors.toList());

            try {
                UserDto findUser = userService.selectUser(no);
                if (userDto != null) {
                    userDto.setName(findUser.getName());
                    userDto.setEmail(findUser.getEmail());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("토큰 유효, DB 조회 에러");
            }

            UserDetails userDetails = new CustomUser(userDto);

            // 사용자 정보 객체, 비밀번호, 사용자 권한 목록
            return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        } catch (ExpiredJwtException e) {
            log.warn("expired JWT", authHeader, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("empty or null JWT", authHeader, e.getMessage());
        }

        return null;
    }

    // 토큰 유효성 검사
    public boolean validateToken(String jwt) {
        try {
            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(getShaKey())
                    .build()
                    .parseSignedClaims(jwt);

            Date exp = parsedToken.getPayload().getExpiration();
            log.info("토큰 만료 기간 : " + exp);

            // 만료일이 현재 날짜 이전이면 false.
            return !exp.before(new Date());
        } catch (ExpiredJwtException e) {
            log.error("Token Expired");         // 토큰 만료
            return false;
        } catch (JwtException e) {
            log.error("Token Tampered");        // 토큰 변조
            return false;
        } catch (NullPointerException e) {
            log.error("Token is Null");         // 토큰 없음
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // secretKey -> signingKey
    private byte[] getSigningKey() {
        return jwtProp.getSecretKey().getBytes();
    }

    // secretKey -> (HMAC-SHA 알고리즘) -> signingKey
    private SecretKey getShaKey() {
        return Keys.hmacShaKeyFor(getSigningKey());
    }
}
