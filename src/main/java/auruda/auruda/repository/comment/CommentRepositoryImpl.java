package auruda.auruda.repository.comment;

import auruda.auruda.domain.Comment;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.QComment;
import auruda.auruda.enums.DeleteStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 댓글 레파지토리 커스텀 구현체 클래스
 */
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory query;
    public CommentRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }


    @Override
    public Page<Comment> findAllByMember(Member member, Pageable pageable) {
        QComment comment = QComment.comment;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(comment.member.eq(member));
        builder.and(comment.deleteStatus.eq(DeleteStatus.ACTIVE));

        List<Comment> content = query.select(comment)
                .from(comment)
                .where(builder)
                .orderBy(comment.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query.select(comment.count())
                .from(comment)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total!=null?total:0);
    }
}
