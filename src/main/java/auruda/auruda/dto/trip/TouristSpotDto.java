package auruda.auruda.dto.trip;

import lombok.Getter;
import lombok.Setter;

/**
 * 여행지를 저장할 DTO
 */
@Getter
@Setter
public class TouristSpotDto {

    private Long placeId; //장소 아이디
    private Long visitedCount; //방문자 수
    private String city; //지역
    private String address; //주소
    private String areaCode; //지역 코드
    private String sigunguCode; //시군구 코드
    private String category; //카테고리
    private String imageUrl; //이미지 URL
    private String name; //장소 이름
    private double lat; //위도
    private double lng; //경도

    /**
     * [생성 메서드]
     * @return TouristSpotDto
     */
    public static TouristSpotDto create(Long placeId,Long visitedCount,String city,String address,String areaCode,String sigunguCode,String category,String photoUrl,String name,double lat,double lng){
        TouristSpotDto dto = new TouristSpotDto();
        dto.setPlaceId(placeId);
        dto.setVisitedCount(visitedCount);
        dto.setCity(city);
        dto.setAddress(address);
        dto.setAreaCode(areaCode);
        dto.setSigunguCode(sigunguCode);
        dto.setCategory(category);
        dto.setImageUrl(photoUrl);
        dto.setName(name);
        dto.setLat(lat);
        dto.setLng(lng);
        return dto;
    }

}
