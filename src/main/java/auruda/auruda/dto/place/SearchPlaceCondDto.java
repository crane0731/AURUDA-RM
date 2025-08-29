package auruda.auruda.dto.place;

import auruda.auruda.enums.Category;
import lombok.Getter;
import lombok.Setter;

/**
 * 장소 검색 조건 DTO
 */
@Getter
@Setter
public class SearchPlaceCondDto {

    private String city; //지역

    private Category category;//카테고리


    /**
     * [생성 메서드]
     * @param city 지역
     * @param category 카테고리
     * @return SearchPlaceCondDto
     */
    public static SearchPlaceCondDto create(String city, Category category) {
        SearchPlaceCondDto dto = new SearchPlaceCondDto();
        dto.city = city;
        dto.category = category;
        return dto;
    }

}
