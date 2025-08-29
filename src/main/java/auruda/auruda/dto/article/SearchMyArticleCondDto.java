package auruda.auruda.dto.article;

import auruda.auruda.enums.ArticleType;
import auruda.auruda.enums.CreatedSortType;
import lombok.Getter;
import lombok.Setter;

/**
 * 자신의 게시글 검색 조건 DTO
 */
@Getter
@Setter
public class SearchMyArticleCondDto {

    private String title;//제목

    private ArticleType articleType; //게시글 타입
    private CreatedSortType created;//생성일 기준

    /**
     * [생성 메서드]
     * @param title 제목
     * @param articleType 게시글 타입
     * @param created 생성일 기준
     * @return SearchMyArticleCondDto
     */
    public static SearchMyArticleCondDto create(String title, ArticleType articleType, CreatedSortType created) {
        SearchMyArticleCondDto dto = new SearchMyArticleCondDto();
        dto.title = title;
        dto.articleType = articleType;
        dto.created = created;
        return dto;
    }

}
