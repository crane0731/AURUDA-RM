package auruda.auruda.dto.review;

import auruda.auruda.domain.Review;
import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 목록 응답 DTO
 */
@Getter
@Setter
public class ReviewListResponseDto {

    private Long reviewId; //리뷰 아이디
    private String content; //리뷰 내용
    private Long rating; //점수

    private Long memberId; //회원 아이디
    private String email; //이메일
    private String nickname; //닉네임


    /**
     * [생성 메서드]
     * @param review 리뷰
     * @return ReviewListResponseDto
     */
    public static ReviewListResponseDto create(Review review) {
        ReviewListResponseDto dto = new ReviewListResponseDto();
        dto.setReviewId(review.getId());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setMemberId(review.getMember().getId());
        dto.setEmail(review.getMember().getEmail());
        dto.setNickname(review.getMember().getNickname());
        return dto;
    }

}
