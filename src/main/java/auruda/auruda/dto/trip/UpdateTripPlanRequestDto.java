package auruda.auruda.dto.trip;

import auruda.auruda.dto.place.CreatePlaceRequestDto;
import auruda.auruda.enums.Theme;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 여행 계획 수정 요청 DTO
 */
@Getter
@Setter
public class UpdateTripPlanRequestDto {

    @NotBlank(message = "제목을 입력하세요.")
    private String title;
    @NotNull(message = "여행기간을 입력하세요.")
    private Long travelDay;
    @NotBlank(message = "여행 시작일을 입력하세요.")
    private String startDate;
    @NotBlank(message = "여행 종료일을 입력하세요.")
    private String endDate;

    @NotBlank(message = "지역을 입력하세요.")
    private String region;

    @NotNull(message = "테마1을 입력하세요.")
    private Theme theme1;
    @NotNull(message = "테마2를 입력하세요.")
    private Theme theme2;
    @NotNull(message = "테마3을 입력하세요.")
    private Theme theme3;

    private List<CreatePlaceRequestDto> places = new ArrayList<>();

}
