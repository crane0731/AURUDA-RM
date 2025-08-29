package auruda.auruda.dto.trip;

import lombok.Getter;
import lombok.Setter;

/**
 * 장소 데이터를 공공 데이터 포탈에서 가져오기 위한 요청 DTO
 */
@Getter
@Setter
public class TouristSpotRequestDto {

    private String city; //지역
    private Integer contentTypeId; //콘텐츠 타입 아이디
    private Integer areaCode; //지역코드
    private Integer sigunguCode; //시군구 코드
    private Integer page; //페이지번호

}
