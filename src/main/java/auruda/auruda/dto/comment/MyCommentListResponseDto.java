package auruda.auruda.dto.comment;

import auruda.auruda.domain.Comment;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 나의 댓글 목록 응답 DTO
 */
@Getter
@Setter
public class MyCommentListResponseDto {

    private Long commentId;//댓글 아이디
    private String content;//내용

    private Long articleId;//게시글 아이디

    private String createdDate;//생성일


    /**
     * [생성 메서드]
     * @param comment 댓글
     * @return MyCommentListResponseDto
     */
    public static MyCommentListResponseDto create(Comment comment) {
        MyCommentListResponseDto dto = new MyCommentListResponseDto();
        dto.setCommentId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setArticleId(comment.getArticle().getId());
        dto.setCreatedDate(DateFormatUtil.DateFormat(comment.getCreatedDate()));
        return dto;
    }


}
