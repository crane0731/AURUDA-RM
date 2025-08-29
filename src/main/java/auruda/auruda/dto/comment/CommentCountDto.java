package auruda.auruda.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 댓글 수 DTO
 */
@Getter
@AllArgsConstructor
public class CommentCountDto {

    private Long articleId; //게시글 아이디
    private Long count;//댓글 수
}
