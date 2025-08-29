package auruda.auruda.controller.login;

import auruda.auruda.config.kakao.KakaoProperties;
import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.token.TokenResponseDto;
import auruda.auruda.service.login.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 카카오 로그인 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoLoginController {

    private final KakaoProperties kakaoProperties; //카카오 설정 클래스
    private final KakaoLoginService kakaoLoginService; //카카오 로그인 서비스

    /**
     * [컨트롤러]
     * 카카오 로그인 시작 - 카카오 인증 페이지로 리다이렉트"
     * @return String
     */
    @GetMapping("/kakao")
    public ResponseEntity<ApiResponseDto<?>> kakaoLogin() {

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id="+kakaoProperties.getClientId() +
                "&redirect_uri=http://localhost:8081/api/auth/kakao/callback" +
                "&response_type=code";

        return ResponseEntity.ok().body(ApiResponseDto.success(kakaoAuthUrl));
    }


    /**
     * [컨트롤러]
     * 카카오 인증 콜백 처리
     * @param code 인증 코드
     * @param response HTTP 응답
     * @return TokenResponseDto
     */
    @GetMapping("/kakao/callback")

    public ResponseEntity<ApiResponseDto<?>> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {

        TokenResponseDto responseDto = kakaoLoginService.kakaoLogin(code);

        // 토큰을 쿼리 파라미터로 붙여서 프론트엔드 특정 페이지로 리다이렉트
        String redirectUrl = "http://localhost:5173/kakao-success"
                + "?accessToken=" + responseDto.getAccessToken()
                + "&refreshToken=" + responseDto.getRefreshToken();

//        response.sendRedirect(redirectUrl);

        return ResponseEntity.ok().body(ApiResponseDto.success(responseDto));

    }

}