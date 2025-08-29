package auruda.auruda.dto.review;

import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.enums.ReviewSortType;
import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 검색 조건 DTO
 */
@Getter
@Setter
public class SearchReviewCondDto {

    private ReviewSortType sortType; //정렬 타입

    private CreatedSortType created; //최신순 or 오래된순


    /**
     * [생성 메서드]
     * @param sortType 정렬 타입
     * @param created 생성일 기준
     * @return SearchReviewCondDto
     */
    public static SearchReviewCondDto create(ReviewSortType sortType, CreatedSortType created) {
        SearchReviewCondDto dto = new SearchReviewCondDto();
        dto.setSortType(sortType);
        dto.setCreated(created);
        return dto;
    }
}
