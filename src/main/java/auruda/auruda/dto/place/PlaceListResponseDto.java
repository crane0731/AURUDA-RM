package auruda.auruda.dto.place;

import auruda.auruda.domain.Place;
import lombok.Getter;
import lombok.Setter;

/**
 * 장소 리스트 응답 DTO
 */
@Getter
@Setter
public class PlaceListResponseDto {

    private Long placeId;//장소 아이디
    private String name;//이름
    private String city;//지역
    private String imageUrl;//이미지 URL
    private Long visitedCount;//방문자 수

    private Long reviewedCount;//리뷰 수
    private Double ratingAvg;//평균 점수


    /**
     * [생성 메서드]
     * @param place 장소
     * @return PlaceListResponseDto
     */
    public static PlaceListResponseDto create(Place place) {
        PlaceListResponseDto dto = new PlaceListResponseDto();
        dto.setPlaceId(place.getId());
        dto.setName(place.getName());
        dto.setCity(place.getCity());
        dto.setImageUrl(place.getImageUrl());
        dto.setVisitedCount(place.getVisitedCount());
        return dto;
    }




}
