package auruda.auruda.dto.place;

import auruda.auruda.domain.Place;
import lombok.Getter;
import lombok.Setter;


/**
 * 장소 정보 응답 DTO
 */
@Getter
@Setter
public class PlaceInfoResponseDto {

    private Long placeId;//장소 아이디
    private String name;//이름
    private String description;//설명

    private String city;//지역
    private String address;//주소
    private Long visitedCount;//방문자수
    private String imageUrl;//이미지 URL

    private Double lat;//위도
    private Double lng;//경도

    private String category;//카테고리

    /**
     * [생성 메서드]
     * @param place 장소
     * @return PlaceInfoResponseDto
     */
    public static PlaceInfoResponseDto create(Place place) {

        PlaceInfoResponseDto dto = new PlaceInfoResponseDto();
        dto.setPlaceId(place.getId());
        dto.setName(place.getName());
        dto.setDescription(place.getDescription());

        dto.setCity(place.getCity());
        dto.setAddress(place.getAddress());
        dto.setVisitedCount(place.getVisitedCount());
        dto.setImageUrl(place.getImageUrl());

        dto.setLat(place.getLat());
        dto.setLng(place.getLng());

        dto.setCategory(place.getCategory().name());
        return dto;

    }

}
