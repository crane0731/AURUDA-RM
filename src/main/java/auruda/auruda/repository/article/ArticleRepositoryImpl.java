package auruda.auruda.repository.article;

import auruda.auruda.domain.Article;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.QArticle;
import auruda.auruda.dto.article.SearchArticleListCondDto;
import auruda.auruda.dto.article.SearchMyArticleCondDto;
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
 * 커스텀 게시글 레파지토리 구현체 클래스
 */
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {


    private final EntityManager em;
    private final JPAQueryFactory query;

    public ArticleRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Page<Article> findAllByCond(SearchArticleListCondDto cond, Pageable pageable) {

        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(article.deleteStatus.eq(DeleteStatus.ACTIVE));

        if(cond.getTitle() != null && !cond.getTitle().isBlank()) {
            builder.and(article.title.containsIgnoreCase(cond.getTitle()));
        }

        if(cond.getNickname() != null && !cond.getNickname().isBlank()) {
            builder.and(article.member.nickname.containsIgnoreCase(cond.getNickname()));
        }

        if(cond.getArticleType()!=null){
            builder.and(article.type.eq(cond.getArticleType()));
        }

        OrderSpecifier<LocalDateTime> order = cond.getCreated() != null ? article.createdDate.asc() : article.createdDate.desc();

        List<Article> content = query.select(article)
                .from(article)
                .join(article.member).fetchJoin()
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(article.count())
                .from(article)
                .where(builder)
                .fetchOne();


        return new PageImpl<>(content, pageable, total!=null?total:0);
    }

    @Override
    public Page<Article> findAllByCondAndMember(Member member, SearchMyArticleCondDto cond, Pageable pageable) {

        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(article.deleteStatus.eq(DeleteStatus.ACTIVE));
        builder.and(article.member.eq(member));

        if(cond.getTitle() != null && !cond.getTitle().isBlank()) {
            builder.and(article.title.containsIgnoreCase(cond.getTitle()));
        }

        if(cond.getArticleType()!=null){
            builder.and(article.type.eq(cond.getArticleType()));
        }

        OrderSpecifier<LocalDateTime> order = cond.getCreated() != null ? article.createdDate.asc() : article.createdDate.desc();

        List<Article> content = query.select(article)
                .from(article)
                .join(article.member).fetchJoin()
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(article.count())
                .from(article)
                .where(builder)
                .fetchOne();


        return new PageImpl<>(content, pageable, total!=null?total:0);
    }
}
