package auruda.auruda.repository.review;

import auruda.auruda.domain.Place;
import auruda.auruda.domain.Review;
import auruda.auruda.dto.review.SearchReviewCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 리뷰 레파지토리 커스텀
 */
public interface ReviewRepositoryCustom {

    Page<Review> findAllByPlaceAndCond(Place place,SearchReviewCondDto cond , Pageable pageable);
}
