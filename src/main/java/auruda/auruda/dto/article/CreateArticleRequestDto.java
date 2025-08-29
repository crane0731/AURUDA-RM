package auruda.auruda.dto.article;

import auruda.auruda.enums.ArticleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 게시글 등록 요청 DTO
 */
@Getter
@Setter
public class CreateArticleRequestDto {

    @NotBlank(message = "제목을 입력해주세요")
    private String title; //제목

    @NotBlank(message = "내용을 입력해주세요")
    private String content; //내용

    @NotNull(message = "게시글 타입을 입력해주세요.")
    private ArticleType articleType; //게시글 타입

    private Long tripPlanId;//여행계획 아이디

    private List<String> imageUrls;// 이미지 URL들

}
