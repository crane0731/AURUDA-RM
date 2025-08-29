package auruda.auruda.dto.comment;

import auruda.auruda.domain.Comment;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 목록 응답 DTO
 */
@Getter
@Setter
public class CommentListResponseDto {

    private Long commentId;//댓글 아이디
    private String content;//내용

    private Long articleId;//게시글 아이디

    private Long memberId;//회원 아이디
    private String memberNickname;//회원 닉네임
    private String memberEmail;//회원 이메일

    private String createdDate;//생성일

    private List<CommentListResponseDto> children=new ArrayList<>();//대댓글들


    /**
     * [생성 메서드]
     * @param comment 댓글
     * @return CommentListResponseDto
     */
    public static CommentListResponseDto create(Comment comment) {
        CommentListResponseDto dto = new CommentListResponseDto();
        dto.setCommentId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setArticleId(comment.getArticle().getId());
        dto.setMemberId(comment.getMember().getId());
        dto.setMemberNickname(comment.getMember().getNickname());
        dto.setMemberEmail(comment.getMember().getEmail());
        dto.setCreatedDate(DateFormatUtil.DateFormat(comment.getCreatedDate()));

        if(comment.getChildren() != null) {
            comment.getChildren().forEach(child -> dto.getChildren().add(CommentListResponseDto.create(child)));

        }
        return dto;

    }


}
