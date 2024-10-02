package com.example.jwt.config;

import com.example.jwt.security.custom.CustomUserDetailService;
import com.example.jwt.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// Security 설정 파일.
// SpringSecurity 5.4 이상에서는 WebSecurityConfigurerAdapter를 사용하지 않음
// @EnableWebSecurity
// public class SecurityConfig extends WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 빈 등록
    private AuthenticationManager authenticationManager;

    @Bean
    public AuthenticationManager getAuthenticationManager
            (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
        return authenticationManager;
    }

    // SpringSecurity 5.5 이상
    // Security 설정 비활성화 작업
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 시큐리티를 당장 쓰지 않을 것이므로 기본적인 설정 비확성화 작업
        // 폼 기반 로그인 비활성화
        http.formLogin((login) -> login.disable());

        // HTTP 기본 인증 비활성화
        http.httpBasic((basic) -> basic.disable());

        // CSRF 공격 방어 비활성화
        http.csrf((csrf) -> csrf.disable());

        // 세션 관리 정책 설정
        // 세션 인증을 사용하지 않고, JWT 토큰을 사용할 것이므로 세션 불필요
        // http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 동일한 함수이나 람다 함수를 이용한 방법으로 사용해야함. 해당 형식은 deprecated
        http.sessionManagement((management) -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 필터 설정
        // 해당 클래스에 매개변수로 설정해둔 AuthenticationManager를 생성자 주입함
        http.addFilterAt(new JwtAuthenticationFilter(authenticationManager), null)
                .addFilterBefore(null, null);

        // 인가 설정
        // 클라이언트 권한별로 요청 경로에 대한 접근 허용을 지정.
        // 람다식. 함수형으로 사용이 권장되어 다른 방법들은 deprecate 됨
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()       // 기타 경로는 인가된 사용자만 가능하도록함.
        );

        // 인증 방식 설정
        // 커스텀 방식으로 설정하여 UserService 클래스의 로직으로 유저를 인증할 것임
        http.userDetailsService(customUserDetailService);

        return http.build();
    }

}
