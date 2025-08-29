package auruda.auruda.repository;

import auruda.auruda.domain.KakaoAccessToken;
import auruda.auruda.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 카카오 엑세스 토큰 레파지토리
 */
public interface KakaoAccessTokenRepository extends JpaRepository<KakaoAccessToken, Long> {


    @Query("SELECT kt " +
            "FROM KakaoAccessToken kt " +
            "WHERE kt.refreshToken.id= :refreshTokenId")
    Optional<KakaoAccessToken> findByRefreshTokenId(@Param("refreshTokenId") Long refreshTokenId);


    Optional<KakaoAccessToken> findByRefreshToken(RefreshToken refreshToken);
}
