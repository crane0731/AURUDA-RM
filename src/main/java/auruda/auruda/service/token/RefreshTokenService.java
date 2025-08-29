package auruda.auruda.service.token;

import auruda.auruda.config.jwt.TokenProvider;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.RefreshToken;
import auruda.auruda.enums.TokenType;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.JwtCustomException;
import auruda.auruda.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

/**
 * 리프레쉬 토큰 서비스
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    private final Duration refreshTokenValidity = Duration.ofDays(1);  // Refresh Token 유효 시간 (1일)


    /**
     * [조회]
     * 회원으로 조회
     * @param member 회원
     * @return RefreshToken
     */
    @Transactional
    public List<RefreshToken> findByMember(Member member) {
        return refreshTokenRepository.findByMember(member);
    }

    /**
     * 회원을 통해 리프레쉬 토큰 삭제
     * @param member 회원
     */
    @Transactional
    public void delete(Member member) {

        //회원으로 리프레쉬 토큰 리스트 조회
        List<RefreshToken> refreshTokenList = refreshTokenRepository.findByMember(member);

        //토큰리스트에 값이 있는지 확인
        existRefreshToken(refreshTokenList);

        //전체 리프레쉬 토큰 삭제
        refreshTokenRepository.deleteAll(refreshTokenList);
    }


    /**
     * [조회]
     * 리프레쉬 토큰으로 조회
     * @param refreshToken 리프레쉬 토큰
     * @return RefreshToken
     */
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->new JwtCustomException(ErrorMessage.NOT_FOUND_REFRESH_TOKEN));
    }


    /**
     * 리프레쉬 토큰 생성 , 저장
     * @param member 회원
     */
    @Transactional
    public String createRefreshToken(Member member) {


        //리프레쉬 토큰 생성
        String token = tokenProvider.generateToken(member, refreshTokenValidity, TokenType.REFRESH);

        //리프레쉬 토큰 저장
        refreshTokenRepository.save(RefreshToken.create(member, token));

        return token;

    }


    //==토큰리스트에 값이 존재 하는지 확인==//
    private void existRefreshToken(List<RefreshToken> refreshTokenList) {
        if (refreshTokenList.isEmpty()) {
            throw new JwtCustomException(ErrorMessage.NOT_FOUND_REFRESH_TOKEN);
        }
    }
}