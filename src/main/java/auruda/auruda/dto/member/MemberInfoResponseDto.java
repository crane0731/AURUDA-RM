package auruda.auruda.dto.member;

import auruda.auruda.domain.Member;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 정보 응답 DTO
 */
@Getter
@Setter
public class MemberInfoResponseDto {

    private Long memberId; //회원 아이디
    private String email; //이메일
    private String nickname; //닉네임
    private Long point; //포인트
    private String grade; //등급
    private String role; //역할

    /**
     * [생성 메서드]
     * @param member 회원
     * @return MemberInfoResponseDto
     */
    public static MemberInfoResponseDto create(Member member){
        MemberInfoResponseDto dto = new MemberInfoResponseDto();
        dto.setMemberId(member.getId());
        dto.setEmail(member.getEmail());
        dto.setNickname(member.getNickname());
        dto.setPoint(member.getPoint());
        dto.setGrade(member.getGrade().name());
        dto.setRole(member.getRole().name());
        return dto;
    }
}
