package auruda.auruda.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 댓글 API 응답 DTO
 */
@Getter
@Setter
public class CommentApiResponseDto {

    private Long count;//댓글 수
    private List<CommentListResponseDto> comments;//댓글들

    /**
     * [생성 메서드]
     * @param count 댓글 수
     * @param comments 댓글 응답 DTO
     * @return CommentApiResponseDto
     */
    public static CommentApiResponseDto create(Long count, List<CommentListResponseDto> comments) {
        CommentApiResponseDto dto = new CommentApiResponseDto();
        dto.count = count;
        dto.comments = comments;
        return dto;
    }

}
