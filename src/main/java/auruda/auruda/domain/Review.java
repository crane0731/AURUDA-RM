package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.ArticleCustomException;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.ReviewCustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.text.AbstractDocument;

/**
 * 리뷰
 */
@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place; //장소

    @Column(name = "content",nullable = false)
    private String content;//내용

    @Column(name = "rating",nullable = false)
    private Long rating; //점수

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태


    /**
     * [생성 메서드]
     * @param member 회원
     * @param place 장소
     * @param content 내용
     * @param rating 점수
     * @return Review
     */
    public static Review create(Member member, Place place, String content, Long rating){
        Review review = new Review();
        review.member = member;
        review.place = place;
        review.content = content;
        review.rating = rating;
        review.deleteStatus = DeleteStatus.ACTIVE;
        return review;
    }

    /**
     * [비즈니스 로직]
     * 리뷰 업데이트
     * @param content 내용
     * @param rating 평점
     */
    public void update(String content, Long rating){
        this.content = content;
        this.rating = rating;
    }


    /**
     * [비즈니스 로직]
     * SOFT DELETE
     */
    public void softDelete(){

        if(this.deleteStatus.equals(DeleteStatus.DELETED)){
            throw new ReviewCustomException(ErrorMessage.ALREADY_DELETED_REVIEW);
        }

        this.deleteStatus = DeleteStatus.DELETED;

    }

}
