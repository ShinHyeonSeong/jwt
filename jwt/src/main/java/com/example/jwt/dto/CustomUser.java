package com.example.jwt.dto;

import com.example.jwt.entity.UserAuthEntity;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.repository.UserAuthRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// spring security 에서 요구하는 UserDetails 형식에 맞추기 위한 클래스
public class CustomUser implements UserDetails {
    @Getter
    private final UserEntity userEntity;

    @Autowired
    private UserAuthRepository userAuthRepository;

    public CustomUser(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    // 권한 getter 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<UserAuthEntity> userAuthEntityList = userAuthRepository.findAllByUserNo_UserNo(userEntity.getUserNo());

        Collection<SimpleGrantedAuthority> roleList = userAuthEntityList.stream()
                .map((auth) -> new SimpleGrantedAuthority(auth.getAuth()))
                .collect(Collectors.toList());

        /**
         Spring Security 에서는 사용자 권한을 GrantedAuthority 인터페이스를 구현한 객체로 표현함이 요구된다.
         SimpleGrantedAuthority 클래스는 GrantedAuthority 인터페이스를 구현한 클래스 중 하나로,
         권한을 문자열로 표현하는 역할을 한다.

         따라서 UserAuthEntity를 그대로 쓰지 않고
         SimpleGrantedAuthority 객체를 통해 GrantedAuthority 타입의 컬렉션에 권한을 담는 것이다.
        **/

        return roleList;
    }

    @Override
    public String getPassword() {
        return userEntity.getUserPw();
    }

    @Override
    public String getUsername() {
        return userEntity.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getEnabled() == 0 ? false : true;
    }
}
