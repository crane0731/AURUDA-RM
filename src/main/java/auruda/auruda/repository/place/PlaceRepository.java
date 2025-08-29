package auruda.auruda.repository.place;

import auruda.auruda.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 장소 레파지토리
 */
public interface PlaceRepository  extends JpaRepository<Place, Long>, PlaceRepositoryCustom {


    @Query("SELECT DISTINCT p " +
            "FROM Place p " +
            "LEFT OUTER JOIN fetch p.reviews AS r " +
            "JOIN fetch r.member " +
            "WHERE p.id=:id")
    Optional<Place> findWithReviewsById(Long id);

    Optional<Place> findByNameAndAddress(String name, String address);

}
