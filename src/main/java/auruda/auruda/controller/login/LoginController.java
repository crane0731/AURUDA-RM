package auruda.auruda.controller.login;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.login.LoginRequestDto;
import auruda.auruda.dto.login.SignUpRequestDto;
import auruda.auruda.dto.token.TokenResponseDto;
import auruda.auruda.service.login.LoginService;
import auruda.auruda.util.ErrorCheckUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/auth")
public class LoginController {

    private final LoginService loginService; //로그인 서비스

    /**
     * [컨트롤러]
     * 회원 가입
     * @param requestDto 회원 가입 요청 DTO
     * @param bindingResult 에러메시지를 바인딩할 객체
     * @return 성공 메시지
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<?>> signUp(@Valid @RequestBody SignUpRequestDto requestDto, BindingResult bindingResult) {

        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        loginService.signUp(requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "회원가입 성공")));

    }


    /**
     * [컨트롤러]
     * 로그인
     * @param loginRequestDto 로그인 요청 DTO
     * @param bindingResult 에러메시지를 바인딩할 객체
     * @return TokenResponseDto
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<?>> login(@Valid @RequestBody LoginRequestDto loginRequestDto, BindingResult bindingResult) {

        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        //로그인
        TokenResponseDto tokenResponseDto = loginService.login(loginRequestDto);

        return ResponseEntity.ok().body(ApiResponseDto.success(tokenResponseDto));

    }

    /**
     * [컨트롤러]
     * 로그아웃
     * @param request HTTP 요청
     * @return 성공메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<?>> logout(HttpServletRequest request) {

        //로그아웃
        loginService.logout(request);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "로그아웃 성공")));

    }

}
