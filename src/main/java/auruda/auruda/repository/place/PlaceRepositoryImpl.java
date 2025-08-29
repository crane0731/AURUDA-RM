package auruda.auruda.repository.place;

import auruda.auruda.domain.Place;
import auruda.auruda.domain.QPlace;
import auruda.auruda.dto.place.SearchPlaceCondDto;
import auruda.auruda.enums.DeleteStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 장소 레파지토리 커스텀 구현체 클래스
 */
public class PlaceRepositoryImpl implements PlaceRepositoryCustom{

    private final EntityManager em;
    private final JPAQueryFactory query;


    public PlaceRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Page<Place> findAllByCond(SearchPlaceCondDto cond, Pageable pageable) {

        QPlace place = QPlace.place;
        BooleanBuilder builder = new BooleanBuilder();


        builder.and(place.deleteStatus.eq(DeleteStatus.ACTIVE));

        if(cond.getCity() != null && !cond.getCity().isBlank()) {
            builder.and(place.city.containsIgnoreCase(cond.getCity()));
        }

        if(cond.getCategory()!=null){
            builder.and(place.category.eq(cond.getCategory()));
        }

        List<Place> content = query.select(place)
                .from(place)
                .where(builder)
                .orderBy(place.visitedCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(place.count())
                .from(place)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);

    }
}
