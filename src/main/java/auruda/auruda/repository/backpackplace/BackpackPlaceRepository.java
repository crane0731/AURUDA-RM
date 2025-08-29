package auruda.auruda.repository.backpackplace;

import auruda.auruda.domain.Backpack;
import auruda.auruda.domain.BackpackPlace;
import auruda.auruda.domain.Place;
import auruda.auruda.enums.DeleteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 배낭 - 장소 레파지토리
 */
public interface BackpackPlaceRepository extends JpaRepository<BackpackPlace, Long> {

      Optional<BackpackPlace> findByBackpackAndPlace(Backpack backpack, Place place);

      @Query("SELECT BP " +
              "FROM BackpackPlace  BP " +
              "JOIN FETCH BP.place " +
              "WHERE BP.backpack=:backpack and BP.deleteStatus='ACTIVE'")
      List<BackpackPlace> findByBackpack(@Param("backpack") Backpack backpack);

}
