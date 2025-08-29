package auruda.auruda.repository.like;

import auruda.auruda.domain.LikeArticle;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 게시글 좋아요 레파지토리
 */
public interface LikeArticleRepository extends JpaRepository<LikeArticle,Long> ,LikeArticleRepositoryCustom{

}
