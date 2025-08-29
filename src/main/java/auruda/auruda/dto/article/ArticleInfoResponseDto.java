package auruda.auruda.dto.article;

import auruda.auruda.domain.Article;
import auruda.auruda.domain.ArticleImage;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 게시글 상세 정보 응답 DTO
 */
@Getter
@Setter
public class ArticleInfoResponseDto {

    private Long articleId;//게시글 아이디
    private String title;//제목
    private String content;//내용
    private List<String> imageUrls; //이미지 URL 목록

    private Long likeCount;//좋아요 수
    private Long viewCount;//조회 수

    private Long memberId;//회원 아이디
    private String memberNickname; //회원 닉네임
    private String memberEmail; //회원 이메일

    private Long tripPlanId; //여행 계획 아이디

    private String createdDate; //생성일
    private String updatedDate; //수정일


    /**
     * [생성 메서드]
     * @param article 게시글
     * @return ArticleInfoResponseDto
     */
    public static ArticleInfoResponseDto create(Article article) {
        ArticleInfoResponseDto dto = new ArticleInfoResponseDto();

        dto.setArticleId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());

        List<String> imageUrls = article.getArticleImages().stream().map(ArticleImage::getImageUrl).toList();
        dto.setImageUrls(imageUrls);

        dto.setLikeCount(article.getLikeCount());
        dto.setViewCount(article.getViewCount());

        dto.setMemberId(article.getMember().getId());
        dto.setMemberNickname(article.getMember().getNickname());
        dto.setMemberEmail(article.getMember().getEmail());

        if(article.getTripPlan() != null) {
            dto.setTripPlanId(article.getTripPlan().getId());

        }

        dto.setCreatedDate(DateFormatUtil.DateFormat(article.getCreatedDate()));
        dto.setUpdatedDate(DateFormatUtil.DateFormat(article.getUpdatedDate()));

        return dto;

    }









}
