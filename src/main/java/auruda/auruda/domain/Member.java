package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.dto.login.SignUpRequestDto;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.enums.MemberGrade;
import auruda.auruda.enums.MemberRole;
import auruda.auruda.exception.ArticleCustomException;
import auruda.auruda.exception.ErrorMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원
 */
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; // PK

    @Column(name = "email", nullable = false)
    private String email; //이메일

    @Column(name = "password", nullable = false)
    private String password; //패스워드

    @Column(name = "nickname", nullable = false)
    private String nickname; //닉네임

    @Column(name = "point")
    private Long point; //포인트

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    private MemberGrade grade; //등급

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private MemberRole role; //역할

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    /**
     * [생성 메서드]
     * @param dto 회원 가입 요청 DTO
     * @return Member
     */
    public static Member create(SignUpRequestDto dto){
        Member member = new Member();
        member.email = dto.getEmail();
        member.password = dto.getPassword();
        member.nickname = dto.getNickname();
        member.point=0L;
        member.grade=MemberGrade.E;
        member.role=MemberRole.MEMBER;
        member.deleteStatus=DeleteStatus.ACTIVE;
        return member;

    }

    /**
     * [비즈니스 로직]
     * 닉네임 업데이트
     * @param newNickname 새로운 닉네임
     */
    public void updateNickname(String newNickname){
        this.nickname = newNickname;
    }

    /**
     * [비즈니스 로직]
     * 패스워드 업데이트
     * @param password 새로운 패스워드
     */
    public void updatePassword(String password){
        this.password=password;
    }

    /**
     * [비즈니스 로직]
     * SOFT DELETE
     */
    public void softDelete(){

        if(this.deleteStatus.equals(DeleteStatus.DELETED)){
            throw new ArticleCustomException(ErrorMessage.ALREADY_DELETED_MEMBER);
        }

        //익명 회원 처리
        anonymizeMemberInfo();

    }

    //==익명 회원 처리==//
    private void anonymizeMemberInfo() {
        this.email = "삭제된 이메일";
        this.nickname = "탈퇴한 회원";
        this.password = "삭제된 비밀번호";
        this.point = 0L;
        this.grade = null;
        this.role = null;
        this.deleteStatus = DeleteStatus.DELETED;
    }

}
