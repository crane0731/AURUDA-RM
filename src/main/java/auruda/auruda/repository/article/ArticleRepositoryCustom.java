package auruda.auruda.repository.article;

import auruda.auruda.domain.Article;
import auruda.auruda.domain.Member;
import auruda.auruda.dto.article.SearchArticleListCondDto;
import auruda.auruda.dto.article.SearchMyArticleCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 게시글 레파지토리 커스텀
 */
public interface ArticleRepositoryCustom {

    Page<Article> findAllByCond(SearchArticleListCondDto cond, Pageable pageable);

    Page<Article>findAllByCondAndMember(Member member, SearchMyArticleCondDto cond, Pageable pageable);

}
