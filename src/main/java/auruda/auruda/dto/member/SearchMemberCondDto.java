package auruda.auruda.dto.member;

import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.enums.MemberGrade;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 검색 조건 DTO
 */
@Getter
@Setter
public class SearchMemberCondDto {

    private String email;//이메일
    private String nickname;//닉네임
    private MemberGrade grade;//등급
    private CreatedSortType created; //최신순 or 오래된순


    /**
     * [생성 메서드]
     * @param email 이메일
     * @param nickname 닉네임
     * @param grade 등급
     * @param created //최신순 or 오래된순
     * @return SearchMemberCondDto
     */
    public static SearchMemberCondDto create(String email, String nickname, MemberGrade grade, CreatedSortType created) {
        SearchMemberCondDto dto = new SearchMemberCondDto();
        dto.setEmail(email);
        dto.setNickname(nickname);
        dto.setGrade(grade);
        dto.setCreated(created);
        return dto;
    }

}
