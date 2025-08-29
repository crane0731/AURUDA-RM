package auruda.auruda.dto.token;

import lombok.Getter;
import lombok.Setter;

/**
 * 리프레쉬 토큰 + 엑세스 토큰 응답 DTO
 */
@Getter
@Setter
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;

    /**
     * [생성 메서드]
     * @param accessToken 엑세스 토큰
     * @param refreshToken 리프레쉬 토큰
     * @return TokenResponseDto
     */
    public static TokenResponseDto create(String accessToken, String refreshToken) {
        TokenResponseDto dto = new TokenResponseDto();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        return dto;
    }


}
