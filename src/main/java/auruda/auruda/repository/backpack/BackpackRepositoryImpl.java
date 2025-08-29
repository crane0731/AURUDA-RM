package auruda.auruda.repository.backpack;

import auruda.auruda.domain.Backpack;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.QBackpack;
import auruda.auruda.dto.backpack.SearchBackpackCondDto;
import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.enums.DeleteStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 배낭 레파지토리 커스텀 구현체
 */
public class BackpackRepositoryImpl implements BackpackRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public BackpackRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Page<Backpack> findAllByMemberAndCond(Member member, SearchBackpackCondDto cond, Pageable pageable) {

        QBackpack backpack = QBackpack.backpack;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(backpack.member.id.eq(member.getId()));
        builder.and(backpack.deleteStatus.eq(DeleteStatus.ACTIVE));

        OrderSpecifier<LocalDateTime> order =
                cond.getCreated() == CreatedSortType.ASC ? backpack.createdDate.asc() : backpack.createdDate.desc();

        List<Backpack> content = query.select(backpack)
                .from(backpack)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(backpack.count())
                .from(backpack)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);


    }
}
