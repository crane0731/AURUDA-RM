package auruda.auruda.service.token;

import auruda.auruda.config.jwt.TokenProvider;
import auruda.auruda.domain.Member;
import auruda.auruda.enums.TokenType;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.JwtCustomException;
import auruda.auruda.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 토큰 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    /**
     * 리프레쉬 토큰을 통해 새로운 엑세스 토큰을 생성
     * @param refreshToken 리프레쉬 토큰
     * @return String
     */
    public String createNewAccessToken(String refreshToken) throws JwtCustomException {

        //토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validRefreshToken(refreshToken)) {
            throw new JwtCustomException(ErrorMessage.INVALID_TOKEN);
        }

        Member member = memberService.findById(refreshTokenService.findByRefreshToken(refreshToken).getMember().getId());

        return tokenProvider.generateToken(member, Duration.ofHours(2), TokenType.ACCESS);

    }
}
