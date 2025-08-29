package auruda.auruda.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 수정 요청 DTO
 */
@Getter
@Setter
public class UpdateReviewRequestDto {

    @NotBlank(message = "리뷰 내용을 입력하세요.")
    private String content; //내용

    @NotNull(message = "평점을 입력해주세요. (0~5)")
    private Long rating;//평점

}
