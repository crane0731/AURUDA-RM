package auruda.auruda.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT를 위해 설정파일에 추가한 프로퍼티 값을 변수로 접근하는데 사용할 클래스
 */
@Setter
@Getter
@Component
@ConfigurationProperties("jwt") //자바 클래스에 프로퍼티 값을 가져와 사용 하는 어노테이션
public class JwtProperties {

    private String issuer;
    private String accessSecretKey;
    private String refreshSecretKey;


}
