package auruda.auruda.dto.member;

import auruda.auruda.domain.Member;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 리스트 응답 DTO
 */
@Getter
@Setter
public class MemberListResponseDto {

    private Long memberId;//회원 아이디
    private String email; //이메일
    private String nickname; //닉네임
    private String grade; //등급


    /**
     * [생성 메서드]
     * @param member 회원
     * @return MemberInfoResponseDto
     */
    public static MemberListResponseDto create(Member member) {
        MemberListResponseDto dto = new MemberListResponseDto();
        dto.setMemberId(member.getId());
        dto.setEmail(member.getEmail());
        dto.setNickname(member.getNickname());
        dto.setGrade(member.getGrade().name());

        return dto;
    }

}
