package auruda.auruda.enums;

/**
 * 회원 등급
 */
public enum MemberGrade {

    A("바람지기"),
    B("길잡이"),
    C("나그네"),
    D("떠돌이"),
    E("찾길이");

    private String commentary;

    MemberGrade(String commentary) {
        this.commentary = commentary;
    }

}
