package com.example.jwt.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

// application.properties에 선언한 비밀키를 가져옴.
@Data
@Component      // 해당 클래스를 빈으로 등록
@ConfigurationProperties("com.example.jwt") // com.example.jwt 하위 속성들을 지정
public class JwtProp {
    // com.example.jwt.secret-key -> secretKey (인코딩된 키)
    // properties에 정의된 시크릿 키임
    private String secretKey;
}
