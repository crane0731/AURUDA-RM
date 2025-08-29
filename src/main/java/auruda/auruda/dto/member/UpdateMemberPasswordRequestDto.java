package auruda.auruda.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 비밀번호 수정 요청 DTO
 */
@Getter
@Setter
public class UpdateMemberPasswordRequestDto {

    @NotBlank(message = "패스워드를 입력하세요.")
    private String password; //패스워드

    @NotBlank(message = "패스워드 체크를 입력하세요.")
    private String passwordCheck; //패스워드 체크

}
