package auruda.auruda.dto.backpack;

import auruda.auruda.domain.Backpack;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 배낭 목록 응답 DTO
 */
@Getter
@Setter
public class BackpackListResponseDto {

    private Long backpackId;//배낭 아이디
    private String name; //배낭 이름
    private String createdDate; //생성일


    /**
     * [생성 메서드]
     * @param backpack 배낭
     * @return BackpackListResponseDto
     */
    public static BackpackListResponseDto create(Backpack backpack) {
        BackpackListResponseDto dto = new BackpackListResponseDto();
        dto.backpackId = backpack.getId();
        dto.name = backpack.getName();
        dto.createdDate= DateFormatUtil.DateFormat(backpack.getCreatedDate());
        return dto;
    }


}
