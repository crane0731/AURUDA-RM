package auruda.auruda.dto.trip;

import auruda.auruda.domain.Place;
import auruda.auruda.domain.TripPlace;
import lombok.Getter;
import lombok.Setter;

/**
 * 여행 장소 목록 응답 DTO
 */
@Getter
@Setter
public class TripPlaceListResponseDto {

    private Long placeId;//장소 아이디
    private Long day; //일차
    private String name; //이름
    private String city; //지역
    private String address; //주소
    private String category; //카테고리
    private String description; //설명

    private double lat; //위도
    private double lng; //경도
    private String imageUrl; //이미지 URL

    /**
     * [생성 메서드]
     * @param tripPlace 여행 장소
     * @return TripPlaceListResponseDto
     */
    public static TripPlaceListResponseDto create(TripPlace tripPlace) {


        //장소 엔티티
        Place place = tripPlace.getPlace();

        TripPlaceListResponseDto dto = new TripPlaceListResponseDto();
        dto.setPlaceId(place.getId());
        dto.setDay(tripPlace.getDay());
        dto.setName(place.getName());
        dto.setCity(place.getCity());
        dto.setAddress(place.getAddress());
        dto.setCategory(place.getCategory().name());
        dto.setDescription(place.getDescription());
        dto.setLat(place.getLat());
        dto.setLng(place.getLng());
        dto.setImageUrl(place.getImageUrl());
        return dto;


    }
}
