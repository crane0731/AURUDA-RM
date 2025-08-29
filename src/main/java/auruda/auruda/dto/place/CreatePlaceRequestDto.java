package auruda.auruda.dto.place;

import auruda.auruda.enums.Category;
import lombok.Getter;
import lombok.Setter;

/**
 * 장소 생성 요청 DTO
 */
@Getter
@Setter
public class CreatePlaceRequestDto {

    private Long day; //일차
    private String name; //이름
    private String city; //지역
    private String address; //주소
    private Category category; //카테고리
    private String description; //설명

    private double lat;//위도
    private double lng; //경도
    private String image_url;//이미지 URL

}
