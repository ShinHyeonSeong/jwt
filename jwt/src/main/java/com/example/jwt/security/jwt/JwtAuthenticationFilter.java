package com.example.jwt.security.jwt;

import com.example.jwt.dto.CustomUser;
import com.example.jwt.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.example.jwt.security.jwt.constants.JwtConstants.AUTH_LOGIN_URL;

/**
 * client -> filter (현재 클래스 필터 설정) -> server
 * 1. 요청
 * 2. attemptAuthentication()
 *          - 실패 : response 401
 *          - 성공 : successfulAuthentication()
 * 3. successfulAuthentication()
 *          - JWT 토큰 생성
 *          - response > header > authorization(JWT)
 */

// client 요청에 대해 서버 응답 이전에 해당 필터가 작동하게 된다.
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // 생성자 주입
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        // Filter URL 경로 설정
        setFilterProcessesUrl(AUTH_LOGIN_URL);
    }

    // 인증 시도에 관한 필터 메서드
    // /login 경로 요청 시 해당 필터로 인증을 시도할 것임
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String userName = request.getParameter("username");
        String userPw = request.getParameter("password");

        log.info("user name : " + userName);
        log.info("user pw : " + userPw);

        // 사용자 인증 정보 객체
        Authentication authentication = new UsernamePasswordAuthenticationToken(userName, userPw);
        // 사용자 인증 (로그인) 절차
        // 해당 메서드 실행 시 UserDetailService로 정의한 커스텀 인증 방식이 작동됨
        // 또한 사용자 패스워드는 PasswordEncoder를 통해 암호화됨
        authentication = authenticationManager.authenticate(authentication);

        log.info("인증 여부 : " + authentication.isAuthenticated());

        if (!authentication.isAuthenticated()) {
            response.setStatus(401);        // 인증 실패 401
        }

        return authentication;
    }

    // 인증 성공 시에 대한 필터 메서드
    // attemptAuthentication 메서드 통과 시 JWT 토큰을 생성함
    // 응답 헤더에 JWT를 설정해줌
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        log.info("인증 성공");

        CustomUser customUser = (CustomUser)authentication.getPrincipal();
        UserEntity userEntity = customUser.getUserEntity();

    }
}
