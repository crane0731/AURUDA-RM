package auruda.auruda.repository.review;

import auruda.auruda.domain.Member;
import auruda.auruda.domain.Place;
import auruda.auruda.domain.Review;
import auruda.auruda.dto.review.ReviewStatsDto;
import auruda.auruda.enums.DeleteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 레파지토리
 */
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("SELECT AVG(r.rating) " +
            "FROM Review r " +
            "WHERE r.place=:place AND r.deleteStatus=:deleteStatus")

    Double getRatingsAverageByPlace(@Param("place") Place place,@Param("deleteStatus") DeleteStatus deleteStatus);

    Optional<Review> findByMemberAndPlace(@Param("member") Member member, @Param("place") Place place);

    @Query("SELECT count (r) " +
            "FROM Review r " +
            "WHERE r.place=:place AND r.deleteStatus=:deleteStatus")
    Long getReviewCount(@Param("place") Place place,@Param("deleteStatus") DeleteStatus deleteStatus);



    @Query("select new auruda.auruda.dto.review.ReviewStatsDto(r.place.id, avg(r.rating), count(r.id)) " +
            "from Review r " +
            "where r.place.id in :placeIds and r.deleteStatus = 'ACTIVE' " +
            "group by r.place.id")
    List<ReviewStatsDto> findStatsByPlaceIds(@Param("placeIds") List<Long> placeIds);


}
