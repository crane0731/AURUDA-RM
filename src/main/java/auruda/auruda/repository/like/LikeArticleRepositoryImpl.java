package auruda.auruda.repository.like;

import auruda.auruda.domain.LikeArticle;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.QLikeArticle;
import auruda.auruda.enums.DeleteStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 게시글 좋아요 레파지토리 커스텀 구현체 클래스
 */
public class LikeArticleRepositoryImpl implements LikeArticleRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public LikeArticleRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Page<LikeArticle> findAllByMember(Member member, Pageable pageable) {

        QLikeArticle likeArticle = QLikeArticle.likeArticle;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(likeArticle.deleteStatus.eq(DeleteStatus.ACTIVE));
        builder.and(likeArticle.member.eq(member));

        List<LikeArticle> content = query.select(likeArticle)
                .from(likeArticle)
                .join(likeArticle.article).fetchJoin()
                .where(builder)
                .orderBy(likeArticle.deleteStatus.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(likeArticle.count())
                .from(likeArticle)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total!=null ? total : 0);

    }
}
