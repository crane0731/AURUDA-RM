package auruda.auruda.dto.backpack;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 배낭 이름 수정 요청 DTO
 */
@Getter
@Setter
public class UpdateBackpackNameRequestDto {

    @NotBlank(message = "배낭 이름을 입력해주세요.")
    private String name; //배낭 이름

}
