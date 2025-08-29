package auruda.auruda.repository.tripplan;

import auruda.auruda.domain.Member;
import auruda.auruda.domain.TripPlan;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.email.TripPlanEmailDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 여행 계획 레파지토리
 */
public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {


    @Query("SELECT new auruda.auruda.dto.email.TripPlanEmailDto(tp.title, m.email, tp.region,tp.startDate, tp.endDate) " +
            "FROM TripPlan tp JOIN tp.member m WHERE tp.startDate = :startDate")
    List<TripPlanEmailDto> findEmailDetailsByStartDate(@Param("startDate") String startDate);


    @Query("SELECT distinct tp " +
            "FROM TripPlan  tp " +
            "LEFT JOIN fetch  tp.tripPlaces tpc " +
            "LEFT JOIN fetch tpc.place " +
            "WHERE tp.id=:id and tp.deleteStatus ='ACTIVE'")
    Optional<TripPlan> findOne(Long id);


    @Query("SELECT tp " +
            "FROM TripPlan tp " +
            "WHERE tp.member=:member and tp.deleteStatus='ACTIVE'")
    Page<TripPlan> findAllByMember(Member member, Pageable pageable);

}
