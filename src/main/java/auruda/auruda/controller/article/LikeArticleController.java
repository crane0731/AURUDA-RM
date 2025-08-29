package auruda.auruda.controller.article;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.article.SearchArticleListCondDto;
import auruda.auruda.enums.ArticleType;
import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.service.article.LikeArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 게시글 좋아요 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/article")
public class LikeArticleController {

    private final LikeArticleService likeArticleService;//게시글 좋아요 서비스

    /**
     * [컨트롤러]
     * 게시글 좋아요
     * @param id 게시글 아이디
     * @return 성공 메시지
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponseDto<?>> like(@PathVariable Long id) {

        likeArticleService.like(id);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "게시글 좋아요 성공")));
    }

    /**
     * [컨트롤러]
     * 회원이 좋아요한 게시글 리스트 목록
     * @param page 페이지 번호
     * @return PagedResponseDto<ArticleListResponseDto>
     */
    @GetMapping("/like")
    public ResponseEntity<ApiResponseDto<?>> getAllArticles(@RequestParam(value = "page",defaultValue = "0")int page
    ){

        return ResponseEntity.ok(ApiResponseDto.success(likeArticleService.getLikeArticles(page)));

    }




}
