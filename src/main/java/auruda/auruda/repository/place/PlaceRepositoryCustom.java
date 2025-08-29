package auruda.auruda.repository.place;

import auruda.auruda.domain.Place;
import auruda.auruda.dto.place.SearchPlaceCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 장소 레파지토리 커스텀
 */
public interface PlaceRepositoryCustom {

    Page<Place> findAllByCond(SearchPlaceCondDto cond, Pageable pageable);

}
