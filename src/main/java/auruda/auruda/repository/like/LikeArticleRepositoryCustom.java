package auruda.auruda.repository.like;

import auruda.auruda.domain.LikeArticle;
import auruda.auruda.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 게시글 좋아요 레파지토리 커스텀
 */
public interface LikeArticleRepositoryCustom {

    Page<LikeArticle> findAllByMember(Member member, Pageable pageable);


}
