package auruda.auruda.repository.comment;

import auruda.auruda.domain.Comment;
import auruda.auruda.dto.comment.CommentCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 댓글 레파지토리
 */
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query("SELECT new auruda.auruda.dto.comment.CommentCountDto(c.article.id , count(c.article.id)) " +
            "FROM Comment c " +
            "WHERE c.id in :articleIds " +
            "GROUP BY c.article.id")
    List<CommentCountDto> findCommentCount(@Param("articleIds")List<Long> articleIds);


    @Query("SELECT c " +
            "FROM Comment  c " +
            "LEFT JOIN FETCH c.children " +
            "JOIN FETCH c.member " +
            "WHERE c.article.id=:articleId and c.parent is null ")
    List<Comment> findCommentByArticleId(Long articleId);

    @Query("SELECT count (c) " +
            "FROM Comment c " +
            "WHERE c.article.id=:articleId and c.deleteStatus='ACTIVE'")
    Long countByArticleId(Long articleId);
}
