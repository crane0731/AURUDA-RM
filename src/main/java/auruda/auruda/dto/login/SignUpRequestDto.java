package auruda.auruda.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 가입 요청 DTO
 */
@Getter
@Setter
public class SignUpRequestDto {

    @NotBlank(message = "이메일 입력해 주세요.")
    private String email;   //이메일

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password; //비밀번호

    @NotBlank(message = "비밀번호 검증을 입력해 주세요.")
    private String passwordCheck;  //비밀번호 검증

    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String nickname; //닉네임

    private String profileImageUrl; //프로필 이미지

    /**
     * [생성 메서드]
     * 카카오 회원 가입을 위한 생성 메서드
     * @param email 이메일
     * @param nickname 닉네임
     * @param profileImageUrl 프로필 이미지 URL
     * @return SignUpRequestDto
     */
    public static SignUpRequestDto createForKakao(String email, String nickname,String profileImageUrl){

        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setEmail(email);
        dto.setNickname(nickname+"@Kakao");
        dto.setProfileImageUrl(profileImageUrl);
        dto.setPassword("0000");
        dto.setPasswordCheck("0000");

        return dto;

    }
}
