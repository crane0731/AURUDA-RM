package auruda.auruda.dto.backpack;

import auruda.auruda.domain.Backpack;
import auruda.auruda.dto.place.PlaceListResponseDto;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 배낭 응답 DTO
 */
@Getter
@Setter
public class BackpackInfoResponseDto {

    private Long backpackId; //배낭 아이디
    private String name; //배낭 이름
    private String createdDate;//생성일

    private List<PlaceListResponseDto> places;//장소들


    /**
     * [생성 메서드]
     * @param backpack 배낭
     * @return BackpackInfoResponseDto
     */
    public static BackpackInfoResponseDto create(Backpack backpack,List<PlaceListResponseDto> places) {
        BackpackInfoResponseDto backpackInfoResponseDto = new BackpackInfoResponseDto();
        backpackInfoResponseDto.backpackId = backpack.getId();
        backpackInfoResponseDto.name = backpack.getName();
        backpackInfoResponseDto.createdDate = DateFormatUtil.DateFormat(backpack.getCreatedDate());
        backpackInfoResponseDto.places = places;
        return backpackInfoResponseDto;
    }
}
