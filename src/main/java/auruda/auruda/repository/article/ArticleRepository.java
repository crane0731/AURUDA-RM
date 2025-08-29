package auruda.auruda.repository.article;

import auruda.auruda.domain.Article;
import auruda.auruda.dto.comment.CommentCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 레파지토리
 */
public interface ArticleRepository extends JpaRepository<Article, Long> ,ArticleRepositoryCustom {

    @Query("SELECT a " +
            "FROM Article a " +
            "JOIN FETCH a.member " +
            "WHERE a.deleteStatus = 'ACTIVE' and a.id=:id")
    Optional<Article> findOne(Long id);

}
