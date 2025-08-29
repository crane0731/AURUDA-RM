package auruda.auruda.dto.backpack;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 배낭 생성 DTO
 */
@Getter
@Setter
public class CreateBackpackRequestDto {

    @NotBlank(message = "배낭 이름을 입력해주세요.")
    private String name; //배낭 이름

}
