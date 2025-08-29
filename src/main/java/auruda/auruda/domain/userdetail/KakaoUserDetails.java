package auruda.auruda.domain.userdetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 카카오 유저 디테일
 */
@Getter
@AllArgsConstructor
@ToString
public class KakaoUserDetails {

    private Long id; //카카오 고유 사용자 ID
    private String nickname; //사용자 닉네임
    private String profileImageUrl;//프로필 이미지 URL
    private String email; // 이메일

}

