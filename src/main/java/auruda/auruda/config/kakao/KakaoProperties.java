package auruda.auruda.config.kakao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 카카오 로그인을 위해 설정파일에 추가한 프로퍼티 값을 변수로 접근하는데 사용할 클래스
 */
@Setter
@Getter
@Component
public class KakaoProperties {

    @Value("${kakao.api.client-id}")
    private String clientId;

    @Value("${kakao.api.client-secret}")
    private String clientSecret;

}

