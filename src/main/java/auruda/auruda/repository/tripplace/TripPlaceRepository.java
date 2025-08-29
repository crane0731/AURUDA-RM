package auruda.auruda.repository.tripplace;

import auruda.auruda.domain.TripPlace;
import auruda.auruda.domain.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 여행 장소 레파지토리
 */
public interface TripPlaceRepository extends JpaRepository<TripPlace, Long> {

    @Query("DELETE  " +
            "FROM TripPlace tp " +
            "WHERE tp.tripPlan=:tripPlan and tp.deleteStatus = 'ACTIVE'")
    void deleteByTripPlan(TripPlan tripPlan);
}
