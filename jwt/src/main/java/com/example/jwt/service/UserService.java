package com.example.jwt.service;

import com.example.jwt.dto.UserAuthDto;
import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.UserAuthEntity;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.repository.UserAuthRepository;
import com.example.jwt.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserAuthRepository userAuthRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
    }

    @Autowired
    private AuthenticationManager authenticationManager;    // 인증 관리자 객체
    @Autowired
    private PasswordEncoder passwordEncoder;                // 패스워드 암호화

    public int join(UserDto userDto) {
        String encodePw = passwordEncoder.encode(userDto.getUserPw());
        userDto.setUserPw(encodePw);
        UserDto saveUserDto = insertUser(userDto);

        if (saveUserDto != null) {
            UserAuthDto userAuthDto = UserAuthDto.builder()
                    .userNo(saveUserDto.toUserEntity())
                    .auth("ROLE_USER")
                    .build();
            UserAuthDto saveUserAuthDto = insertUserAuth(userAuthDto);
        }
        else return 0;
        return 1;
    }

    public void login(UserDto userDto, HttpServletRequest request) throws Exception {
        String userName = userDto.getName();
        String password = userDto.getUserPw();
        log.info("user ID : " + userName);
        log.info("user PW : " + password);

        // AuthenticationManager
        // 아이디, 패스워드 인증 토큰 생성
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(userName, password);

        // 토큰에 요청 정보를 등록
        // 해당 정보가 실제로 들어온 요청인지, 위변조 등의 검증을 겸함
        token.setDetails(new WebAuthenticationDetails(request));

        // 로그인 요청
        /** 토큰 내부의 정보를 통해 인증 과정을 진행함. 인증 방식은 스프링 시큐리티 내부적으로 처리하거나, JDBC를 사용하여
            실제 DB값과 비교하는 방법 등이 있는데, 이는 SecurityFilter에 정의된 방법으로 진행하게 된다. **/
        // 검증 성공 시 Authentication 객체 반환
        Authentication authentication = authenticationManager.authenticate(token);
        log.info("인증 여부 = " + authentication.isAuthenticated());    // true, false

        // 인증 관리자로 로그인된 User 객체를 가져올 수 있음
        // 이는 임의 entity가 아닌 스프링 시큐리티 내부의 User 객체임
        User authUser = (User)authentication.getPrincipal();
        log.info("인증된 사용자 아이디 = " + authUser.getUsername());

        // 시큐리티 컨텍스트에 최종적으로 인증된 정보를 담아주는 작업.
        // CRUD 작업, 컨트롤러 및 비즈니스 로직 등에 사용됨.
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    public UserDto insertUser(UserDto userDto) {
        UserEntity result = userRepository.save(userDto.toUserEntity());
        return UserDto.toUserDto(result);
    }

    public UserDto selectUser(int userNo) {
        UserEntity result = userRepository.findById(userNo)
                .orElse(null);
        return UserDto.toUserDto(result);
    }

    public UserDto updateUser(UserDto userDto) {
        String encodedPw = passwordEncoder.encode(userDto.getUserPw());
        userDto.setUserPw(encodedPw);
        UserEntity userEntity = userDto.toUserEntity();
        UserEntity result = userRepository.save(userEntity);
        return UserDto.toUserDto(result);
    }

    @Transactional
    public int deleteUser(UserDto userDto) {
        Optional<UserEntity> userEntity = userRepository.findById(userDto.getUserNo());
        if (userEntity.isPresent()) {
            userRepository.deleteById(userDto.getUserNo());
        }
        return userDto.getUserNo();
    }

    public UserEntity findUserByUserId(String userId) {
        UserEntity result = userRepository.findByUserId(userId);
        return result;
    }

    public UserAuthDto insertUserAuth(UserAuthDto userAuthDto) {
        UserAuthEntity insertUserAuth = userAuthRepository.save(userAuthDto.toUserAuthEntity());
        UserAuthDto result = new UserAuthDto();
        userAuthDto.toUserAuthDto(insertUserAuth);
        return result;
    }

    public UserAuthDto findUserAuthByUserNo(int userNo) {
        UserAuthEntity result = userAuthRepository.findByUserNo_UserNo(userNo);
        return UserAuthDto.toUserAuthDto(result);
    }

}
