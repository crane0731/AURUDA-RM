package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리프레쉬 토큰
 */
@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //회원

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken; //리프레쉬 토큰

    @OneToOne(mappedBy = "refreshToken",cascade = CascadeType.REMOVE)
    private KakaoAccessToken kakaoAccessToken;


    /**
     * [생성 메서드]
     * @param member 회원
     * @param refreshToken 리프레쉬 토큰
     * @return RefreshToken
     */
    public static RefreshToken create(Member member, String refreshToken) {
        RefreshToken entity = new RefreshToken();
        entity.member = member;
        entity.refreshToken = refreshToken;
        return entity;
    }
}
