package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오 엑세스 토큰
 */
@Entity
@Table(name = "kakao_access_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoAccessToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kakao_access_token_id")
    private Long id; // PK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_token_id")
    private RefreshToken refreshToken; //리프레쉬 토큰

    @Column(name = "access_token" , nullable = false)
    private String accessToken; //엑세스 토큰


    /**
     * [생성 메서드]
     * @param refreshToken 리프레쉬 토큰
     * @param accessToken 엑세스 토큰
     * @return KakaoAccessToken
     */
    public static KakaoAccessToken create(RefreshToken refreshToken,String accessToken) {
        KakaoAccessToken kakaoAccessToken = new KakaoAccessToken();
        kakaoAccessToken.refreshToken = refreshToken;
        kakaoAccessToken.accessToken = accessToken;
        return kakaoAccessToken;
    }


}
