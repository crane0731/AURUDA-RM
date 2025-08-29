package auruda.auruda.dto.backpack;

import auruda.auruda.enums.CreatedSortType;
import lombok.Getter;
import lombok.Setter;

/**
 * 배낭 검색 조건 DTO
 */
@Getter
@Setter
public class SearchBackpackCondDto {

    private CreatedSortType created;


    /**
     * [생성 메서드]
     * @param created 생성일 기준
     * @return SearchBackpackCondDto
     */
    public static SearchBackpackCondDto create(CreatedSortType created) {
        SearchBackpackCondDto dto = new SearchBackpackCondDto();
        dto.setCreated(created);
        return dto;
    }
}
