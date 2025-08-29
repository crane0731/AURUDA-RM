package auruda.auruda.repository.review;

import auruda.auruda.domain.Place;
import auruda.auruda.domain.QReview;
import auruda.auruda.domain.Review;
import auruda.auruda.dto.review.SearchReviewCondDto;
import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.enums.DeleteStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * 리뷰 레파지토리 커스텀 구현체 클래스
 */
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public ReviewRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }


    @Override
    public Page<Review> findAllByPlaceAndCond(Place place, SearchReviewCondDto cond, Pageable pageable) {

        QReview review = QReview.review;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(review.deleteStatus.eq(DeleteStatus.ACTIVE));

        if (place != null) {
            builder.and(review.place.eq(place));
        }

        List<Review> content = query.select(review)
                .from(review)
                .where(builder)
                .join(review.member).fetchJoin()
                .orderBy(createdOrderSpecifier(cond, review))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(review.count())
                .from(review)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);


    }

    //==정렬 타입 객체 생성==//
    private OrderSpecifier<?>[] createdOrderSpecifier(SearchReviewCondDto cond, QReview review) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if(cond.getSortType()!=null) {
            switch (cond.getSortType()){
                case RATING_DESC -> orders.add(review.rating.desc().nullsLast());
                case RATING_ASC -> orders.add(review.rating.asc().nullsLast());

            }
        }

        orders.add(
                cond.getCreated() == null || cond.getCreated() == CreatedSortType.DESC
                        ? review.createdDate.desc().nullsLast()
                        : review.createdDate.asc().nullsLast()
        );
        return orders.toArray(new OrderSpecifier<?>[0]);
    }
}
