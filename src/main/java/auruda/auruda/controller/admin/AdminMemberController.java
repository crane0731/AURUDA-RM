package auruda.auruda.controller.admin;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.member.SearchMemberCondDto;
import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.enums.MemberGrade;
import auruda.auruda.service.admin.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 관리자 회원 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService; //관리자 회원 서비스

    /**
     * [컨트롤러]
     * 관리자 - 회원 삭제
     * @param id 회원 아이디
     * @return 성공 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteMember(@PathVariable("id") Long id) {
        adminMemberService.deleteMember(id);
        return ResponseEntity.ok().body(ApiResponseDto.success(Map.of("message","회원 삭제 성공")));
    }


    /**
     * [컨트롤러]
     * 관리자 - 회원 정보 상세 조회
     * @param id 회원 아이디
     * @return MemberInfoResponseDto
     */
    @GetMapping("/{id}")

    public ResponseEntity<ApiResponseDto<?>> getMemberInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(ApiResponseDto.success(adminMemberService.findMemberInfo(id)));
    }


    /**
     * [컨트롤러]
     * 관리자 - 회원 목록 조회
     * @param email 이메일
     * @param nickname 닉네임
     * @param grade 등급
     * @param created 생성일 기준
     * @param page 페이지 번호
     * @return PagedResponseDto
     */
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<?>> findMemberList(@RequestParam(value = "email", required = false) String email,
                                                            @RequestParam(value = "nickname",required = false)String nickname,
                                                            @RequestParam(value = "grade",required = false)MemberGrade grade,
                                                            @RequestParam(value = "created", required = false) CreatedSortType created,
                                                            @RequestParam(value = "page",defaultValue = "0") int page
                                                            ) {

        return ResponseEntity.ok().body(ApiResponseDto.success(adminMemberService.findMemberList(SearchMemberCondDto.create(email, nickname, grade, created), page)));

    }

}
