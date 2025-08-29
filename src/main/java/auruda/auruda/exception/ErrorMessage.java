package auruda.auruda.exception;
/**
 * 사용자 에러 메시지
 */
public final class  ErrorMessage {

    /**
     * 회원
     */
    public static final String NOT_FOUND_MEMBER = "회원을 찾을 수 없습니다.";
    public static final String ALREADY_DELETED_MEMBER = "이미 삭제된 회원입니다.";
    public static final String DUPLICATED_EMAIL = "이메일이 이미 존재합니다.";
    public static final String DUPLICATED_NICKNAME = "닉네임이 이미 존재합니다.";
    public static final String PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다.";

    public static final String NOT_FOUND_REFRESH_TOKEN = "리프레쉬 토큰을 찾을 수 없습니다.";
    public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
    public static final String FAILED_CREATED_DIRECTORY = "디렉토리 생성에 실패하였습니다.";
    public static final String INCORRECT_FILE_NAME = "올바른 파일 이름이 아닙니다.";
    public static final String NOT_IMAGE_FILE = "이미지 파일이 아닙니다.";
    public static final String FAILED_IMAGE_UPLOAD = "이미지 업로드에 실패하였습니다.";
    public static final String FAILED_DELETE_FILE = "파일 삭제에 실패했습니다.";
    public static final String NOT_FOUND_FILE = "파일을 찾을 수 없습니다.";
    public static final String EXPIRED_TOKEN = "토큰이 만료되었습니다.";
    public static final String LOGOUT_TOKEN = "로그아웃 된 토큰 입니다.";

    public static final String NO_PERMISSION = "권한이 없습니다.";


    /**
     * 게시글
     */
    public static final String NOT_FOUND_ARTICLE = "게시글을 찾을 수 없습니다.";
    public static final String NOT_FOUND_LIKE_ARTICLE = "게시글 좋아요를 찾을 수 없습니다.";

    public static final String ALREADY_DELETED_ARTICLE = "이미 삭제된 게시글 입니다.";
    public static final String ALREADY_DELETED_LIKE_ARTICLE = "이미 삭제된 게시글 좋아요 입니다.";

    /**
     * 댓글
     */
    public static final String NOT_FOUND_COMMENT = "댓글을 찾을 수 없습니다.";
    public static final String ALREADY_EXISTS_PARENT = "이미 부모가 있는 댓글입니다.";

    public static final String ALREADY_DELETED_COMMENT = "이미 삭제된 댓글 입니다.";

    /**
     * 장소
     */
    public static final String NOT_FOUND_PLACE = "장소를 찾을 수 없습니다.";

    public static final String ALREADY_DELETED_PLACE = "이미 삭제된 장소 입니다.";
    public static final String ALREADY_EXISTS_PLACE = "이미 장소가 존재합니다..";


    /**
     * 리뷰
     */
    public static final String NOT_FOUND_REVIEW ="리뷰를 찾을 수 없습니다.";
    public static final String ALREADY_EXISTS_REVIEW="이미 리뷰가 존재합니다.";
    public static final String ALREADY_DELETED_REVIEW="이미 삭제된 리뷰입니다.";
    public static final String REVIEW_RATING_NOT_UNDER_ZERO ="리뷰 점수는 0보다 작을 수 없습니다.";


    /**
     * 배낭
     */
    public static final String NOT_FOUND_BACKPACK ="배낭를 찾을 수 없습니다.";
    public static final String NOT_FOUND_BACKPACK_PLACE ="배낭장소를 찾을 수 없습니다.";
    public static final String ALREADY_EXISTS_BACKPACK="이미 배낭이 존재합니다.";
    public static final String ALREADY_EXISTS_BACKPACK_PLACE="이미 배낭장소가 존재합니다.";

    public static final String ALREADY_DELETED_BACKPACK="이미 삭제된 배낭입니다.";
    public static final String ALREADY_DELETED_BACKPACK_PLACE="이미 삭제된 배낭장소입니다.";


    /**
     * 여행 계획
     */
    public static final String NOT_FOUND_TRIP_PLAN ="여행계획을 찾을 수 없습니다.";
    public static final String ALREADY_DELETED_TRIP_PLAN="이미 삭제된 여행 계획입니다.";

    /**
     * 여행 장소
     */
    public static final String NOT_FOUND_TRIP_PLACE ="여행장소를 찾을 수 없습니다.";
    public static final String ALREADY_DELETED_TRIP_PLACE="이미 삭제된 여행 장소입니다.";


}
