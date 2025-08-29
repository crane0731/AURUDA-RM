package auruda.auruda.dto.article;

import auruda.auruda.enums.ArticleType;
import auruda.auruda.enums.CreatedSortType;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 검색 조건
 */
@Getter
@Setter
public class SearchArticleListCondDto {

    private String title;//제목
    private String nickname;//글쓴이 닉네임

    private ArticleType articleType; //게시글 타입
    private CreatedSortType created;//생성일 기준


    /**
     * [생성 메서드]
     * @param title 제목
     * @param nickname 닉네임
     * @param articleType 게시글 타입
     * @param created 생성일 기준
     * @return SearchArticleListCondDto
     */
    public static SearchArticleListCondDto create(String title, String nickname, ArticleType articleType, CreatedSortType created) {

        SearchArticleListCondDto dto = new SearchArticleListCondDto();
        dto.title = title;
        dto.nickname = nickname;
        dto.articleType = articleType;
        dto.created = created;
        return dto;
    }

}
