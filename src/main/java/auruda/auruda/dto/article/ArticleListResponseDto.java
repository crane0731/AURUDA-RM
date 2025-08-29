package auruda.auruda.dto.article;

import auruda.auruda.domain.Article;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 목록 응답 DTO
 */
@Getter
@Setter
public class ArticleListResponseDto {

    private Long articleId; //게시글 아이디
    private String title;//제목
    private Long viewCount; //조회수
    private Long likeCount; //좋아요 수
    private Long commentCount;//댓글 수

    private Long memberId; //회원 아이디
    private String memberNickname;//회원 닉네임

    private String createDate; //생성일

    /**
     * [생성 메서드]
     * @param article 게시글
     * @return ArticleListResponseDto
     */
    public static ArticleListResponseDto create(Article article) {

        ArticleListResponseDto dto = new ArticleListResponseDto();
        dto.setArticleId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setCommentCount(0L);
        dto.setMemberId(article.getMember().getId());
        dto.setMemberNickname(article.getMember().getNickname());

        dto.setCreateDate(DateFormatUtil.DateFormat(article.getCreatedDate()));
        return dto;
    }



}
