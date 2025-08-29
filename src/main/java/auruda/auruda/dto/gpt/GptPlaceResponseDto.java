package auruda.auruda.dto.gpt;

import lombok.Getter;
import lombok.Setter;

/**
 * GPT 장소 응답 DTO
 */
@Getter
@Setter
public class GptPlaceResponseDto {

    private Long tripPlaceId; //여행장소 아이디
    private Long visitedCount;//방문자수
    private String day; //여행 일
    private String name; //여행지 이름
    private String city; //도시
    private String address; //주소
    private double lat; //위도
    private double lng; //경도
    private String imageUrl;//이미지 URL
    private String description; //설명
    private String category; //카테고리

    /**
     *[생성 메서드]
     * @return GptPlaceResponseDto
     */
    public static GptPlaceResponseDto create(Long tripPlaceId,Long visitedCount,String day, String name, String city, String address, double lat, double lng, String imageUrl, String description, String category) {

        GptPlaceResponseDto dto = new GptPlaceResponseDto();
        dto.tripPlaceId = tripPlaceId;
        dto.visitedCount = visitedCount;
        dto.day = day;
        dto.name = name;
        dto.city = city;
        dto.address = address;
        dto.lat = lat;
        dto.lng = lng;
        dto.imageUrl = imageUrl;
        dto.description = description;
        dto.category = category;
        return dto;
    }


}
