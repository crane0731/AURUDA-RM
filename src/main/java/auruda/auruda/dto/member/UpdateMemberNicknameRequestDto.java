package auruda.auruda.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 닉네임 변경 요청 DTO
 */
@Getter
@Setter
public class UpdateMemberNicknameRequestDto {

    @NotBlank(message = "닉네임을 입력하세요.")
    private String nickname; //회원 닉네임

}
