package auruda.auruda.dto.gpt;

import lombok.Getter;
import lombok.Setter;

/**
 * GPT 장소 요청 DTO
 */
@Getter
@Setter
public class GptPlaceRequestDto {
    private String placeName; //장소이름
    private String category; //카테고리

    /**
     * [생성 메서드]
     * @param placeName 장소이름
     * @param category 카테고리
     * @return GptPlaceRequestDto
     */
    public static GptPlaceRequestDto create(String placeName, String category) {
        GptPlaceRequestDto dto = new GptPlaceRequestDto();
        dto.placeName = placeName;
        dto.category = category;
        return dto;
    }
}
