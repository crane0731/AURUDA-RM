package auruda.auruda.controller.member;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.member.UpdateMemberNicknameRequestDto;
import auruda.auruda.dto.member.UpdateMemberPasswordRequestDto;
import auruda.auruda.service.member.MemberService;
import auruda.auruda.util.ErrorCheckUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/members")
public class MemberController {

    private final MemberService memberService; //회원 서비스

    /**
     * [컨트롤러]
     * 회원 자신의 정보 상세 조회
     * @return MemberInfoResponseDto
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<?>> findMyInfo(){

        return ResponseEntity.ok(ApiResponseDto.success(memberService.findMyInfo()));
    }

    /**
     * [컨트롤러]
     * 회원 닉네임 수정
     * @param requestDto 닉네임 수정 요청 DTO
     * @param bindingResult 에러 메시지를 바인딩할 객체
     * @return 성공 메시지
     */
    @PutMapping("/me/nickname")
    public ResponseEntity<ApiResponseDto<?>> updateNickname(@RequestBody @Valid UpdateMemberNicknameRequestDto requestDto, BindingResult bindingResult){

        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        memberService.updateMyNickname(requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "회원 닉네임 수정 성공")));

    }


    /**
     * [컨트롤러]
     * 비밀번호 수정
     * @param requestDto 비밀번호 수정 요청 DTO
     * @param bindingResult 에러메시지를 바인딩할 객체
     * @return 성공 메시지
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponseDto<?>> updatePassword(@RequestBody @Valid UpdateMemberPasswordRequestDto requestDto,BindingResult bindingResult){


        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        memberService.updateMyPassword(requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "회원 비밀번호 수정 성공")));

    }

    /**
     * [컨트롤러]
     * 회원 탈퇴
     * @return 성공 메시지
     */
    @DeleteMapping("/me/withdraw")
    public ResponseEntity<ApiResponseDto<?>> withdrawMember(){

        memberService.withdraw();
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "회원 탈퇴 성공")));

    }

}
