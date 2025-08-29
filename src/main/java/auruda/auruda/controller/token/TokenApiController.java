package auruda.auruda.controller.token;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.token.TokenResponseDto;
import auruda.auruda.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 토큰 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/auth")
public class TokenApiController {

    private final TokenService tokenService; // 토큰 서비스

    /**
     * [컨트롤러]
     * 새로운 엑세스 토큰 발급
     * @param authorizationHeader 인증 헤더
     * @return TokenResponseDto
     */
    @PostMapping("/token")
    public ResponseEntity<ApiResponseDto<?>> createNewAccessToken(@RequestHeader("Authorization")String authorizationHeader){

        String token = "";

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // "Bearer " 접두사 제거
            token = authorizationHeader.substring(7);
        }

        String newAccessToken = tokenService.createNewAccessToken(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(TokenResponseDto.create(newAccessToken,token)));
    }

}
