package auruda.auruda.controller.article;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.article.CreateArticleRequestDto;
import auruda.auruda.dto.article.SearchArticleListCondDto;
import auruda.auruda.dto.article.SearchMyArticleCondDto;
import auruda.auruda.dto.article.UpdateArticleRequestDto;
import auruda.auruda.enums.ArticleType;
import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.service.article.ArticleService;
import auruda.auruda.util.ErrorCheckUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 게시글 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/article")
public class ArticleController {

    private final ArticleService articleService;//게시글 서비스


    /**
     * [컨트롤러]
     * 게시글 등록
     * @param requestDto 요청 DTO
     * @param bindingResult 에러메시지를 바인딩 할 객체
     * @return 성공 메시지
     */
    @PostMapping("")
    public ResponseEntity<ApiResponseDto<?>> createArticle(@Valid @RequestBody CreateArticleRequestDto requestDto, BindingResult bindingResult) {

        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        articleService.createArticle( requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "게시글 등록 성공")));

    }

    /**
     * [컨트롤러]
     * 게시글 삭제 (SOFT DELETE)
     * @param id 게시글 아이디
     * @return 성공 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "게시글 삭제 성공")));

    }

    /**
     * [컨트롤러]
     * 게시글 수정
     * @param id 게시글 아이디
     * @param requestDto 수정 요청 DTO
     * @param bindingResult 에러 메시지를 바인딩 할 객체
     * @return 성공 메시지
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateArticle(@PathVariable("id")Long id,@Valid @RequestBody UpdateArticleRequestDto requestDto, BindingResult bindingResult) {
        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        articleService.updateArticle(id, requestDto);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "게시글 수정 성공")));


    }

    /**
     * [컨트롤러]
     * 게시글 상세 조회
     * @param id 게시글 아이디
     * @return ArticleInfoResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getArticle(@PathVariable("id") Long id) {

        return ResponseEntity.ok(ApiResponseDto.success(articleService.getArticleInfo(id)));

    }


    /**
     * [컨트롤러]
     * 게시글 목록 조회
     * @param title 제목
     * @param nickname 닉네임
     * @param articleType 게시글 타입
     * @param created 생성일 기준
     * @param page 페이지 번호
     * @return PagedResponseDto<ArticleListResponseDto>
     */
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<?>> getAllArticles(@RequestParam(value = "title",required = false)String title,
                                                            @RequestParam(value = "nickname",required = false)String nickname,
                                                            @RequestParam(value = "articleType",required = false) ArticleType articleType,
                                                            @RequestParam(value = "created",required = false) CreatedSortType created,
                                                            @RequestParam(value = "page",defaultValue = "0")int page
                                                            ){

        return ResponseEntity.ok(ApiResponseDto.success(articleService.getAllArticles(SearchArticleListCondDto.create(title, nickname, articleType, created), page)));

    }

    /**
     * [컨트롤러]
     * 나의 게시글 조회
     * @param title 제목
     * @param articleType 게시글 타입
     * @param created 생성일 기준
     * @param page 페이지 번호
     * @return PagedResponseDto<ArticleListResponseDto>
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<?>> getMyArticles(@RequestParam(value = "title",required = false)String title,
                                                            @RequestParam(value = "articleType",required = false) ArticleType articleType,
                                                            @RequestParam(value = "created",required = false) CreatedSortType created,
                                                            @RequestParam(value = "page",defaultValue = "0")int page
    ){


        return ResponseEntity.ok(ApiResponseDto.success(articleService.getMyArticles(SearchMyArticleCondDto.create(title,articleType, created), page)));

    }

}
