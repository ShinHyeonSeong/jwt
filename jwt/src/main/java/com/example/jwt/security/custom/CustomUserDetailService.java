package com.example.jwt.security.custom;

import com.example.jwt.dto.CustomUser;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 사용자 인증 방식을 설정하기 위한 클래스.
// spring security의 UserDetailService 인터페이스에 커스텀 인증 방식을 구현해 연동한다.
// UserDetailsService - CustomDetailService, UserDetails - CustomUser
@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;

    // Spring Security의 인증 과정에서 유저의 정보를 읽을 때 해당 메서드로 동작하도록 설정
    // 사용자 정보를 가져와서 컨텍스트에 활용할 수 있는 역할도 해줌
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByName : " + username);
        UserEntity userEntity = userService.findUserByUserId(username);

        if (userEntity == null) {
            log.info("no such user");
            throw new UsernameNotFoundException("일치하는 아이디가 없습니다. (" + username + ")");
        }

        log.info("user : " + userEntity.toString());

        // User 객체를 SpringSecurity에서 사용할 수 있도록 UserDetails 인터페이스를 구현한 CustomUser 객체로 변환할 것임
        return new CustomUser(userEntity);
    }
}
