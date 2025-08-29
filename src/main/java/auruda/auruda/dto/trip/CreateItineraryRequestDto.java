package auruda.auruda.dto.trip;

import lombok.Getter;
import lombok.Setter;

/**
 * 여행 계획 생성 요청 DTO
 */
@Getter
@Setter
public class CreateItineraryRequestDto {

    private String startDate; //시작일
    private String endDate; //종료일
    private String travelDay; //여행 일
    private String city; //지역
    private Integer areaCode; //지역코드
    private Integer sigunguCode; //시군구 코드

    private String theme1; //테마1
    private String theme2; //테마2
    private String theme3; //테마3
}
