package auruda.auruda.controller.comment;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.comment.CreateCommentRequestDto;
import auruda.auruda.service.comment.CommentService;
import auruda.auruda.util.ErrorCheckUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * [댓글 서비스]
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/article")
public class CommentController {

    private final CommentService commentService;//댓글 서비스

    /**
     * [컨트롤러]
     * 댓글 등록
     * @param id 게시글 아이디
     * @param requestDto 생성 요청 DTO
     * @param bindingResult 에러메시지를 바인딩 할 객체
     * @return 성공 메시지
     */
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> createComment(@PathVariable("id")Long id, @Valid @RequestBody CreateCommentRequestDto requestDto, BindingResult bindingResult) {
        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        commentService.createComment(id, requestDto);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "댓글 등록 성공")));

    }


    /**
     * [컨트롤러]
     * 댓글 삭제
     * @param id 댓글 아이디
     * @return 성공 메시지
     */
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteComment(@PathVariable("id")Long id) {

        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "댓글 삭제 성공")));

    }

    /**
     * [컨트롤러]
     * 특정 게시글의 댓글 목록 조회
     * @param id 게시글 아이디
     * @return CommentApiResponseDto
     */
    @GetMapping("/{id}/comment")
    public ResponseEntity<ApiResponseDto<?>> getComments(@PathVariable("id")Long id) {
        return ResponseEntity.ok(ApiResponseDto.success(commentService.getAllComments(id)));

    }


    /**
     * [컨트롤러]
     * 자신이 쓴 댓글 목록 조회
     * @param page 페이지 번호
     * @return PagedResponseDto<MyCommentListResponseDto>
     */
    @GetMapping("/comment/me")
    public ResponseEntity<ApiResponseDto<?>> getComments(@RequestParam(value = "page",defaultValue = "0")int page) {

        return ResponseEntity.ok(ApiResponseDto.success(commentService.getMyComments(page)));

    }

}
