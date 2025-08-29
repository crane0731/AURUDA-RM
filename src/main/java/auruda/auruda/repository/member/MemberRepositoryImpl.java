package auruda.auruda.repository.member;

import auruda.auruda.domain.Member;
import auruda.auruda.domain.QMember;
import auruda.auruda.dto.member.SearchMemberCondDto;
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
 * 회원 레파지토리 커스텀 구현체 클래스
 */
public class MemberRepositoryImpl implements MemberRepositoryCustom{


    private final EntityManager em;
    private final JPAQueryFactory query;

    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }


    @Override
    public Page<Member> findAllByCond(SearchMemberCondDto cond, Pageable pageable) {

        QMember member = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        //삭제 상태가 아닌 회원만 조회
        builder.and(member.deleteStatus.eq(DeleteStatus.ACTIVE));

        //이메일 검색
        if(cond.getEmail() != null && !cond.getEmail().isBlank()) {
            builder.and(member.email.containsIgnoreCase(cond.getEmail()));
        }

        //닉네임 검색
        if(cond.getNickname() != null && !cond.getNickname().isBlank()) {
            builder.and(member.nickname.containsIgnoreCase(cond.getNickname()));
        }

        //등급 검색
        if (cond.getGrade() != null) {
            builder.and(member.grade.eq(cond.getGrade()));
        }

        OrderSpecifier<?> order =
                cond.getCreated() == CreatedSortType.ASC ? member.createdDate.asc() : member.createdDate.desc();

        List<Member> content = query
                .select(member)
                .from(member)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = query
                .select(member.count())
                .from(member)
                .where(builder)
                .fetchOne();


        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
