package auruda.auruda.repository;

import auruda.auruda.domain.Member;
import auruda.auruda.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findByMember(Member member);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

}
