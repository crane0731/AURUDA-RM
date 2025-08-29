package auruda.auruda.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 생성 요청 DTO
 */
@Getter
@Setter
public class CreateCommentRequestDto {

    private Long parentId;//부모 댓글 아이디

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;//댓글 내용


}
