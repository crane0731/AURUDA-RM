package auruda.auruda.dto.trip;

import auruda.auruda.domain.TripPlan;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 여행계획 목록 응답 DTO
 */
@Getter
@Setter
public class TripPlanListResponseDto {

    private Long tripPlanId;
    private String title;
    private Long day;//여행 일
    private String createdDate;//생성일

    /**
     * [생성 메서드]
     * @param tripPlan 여행 계획
     * @return TripPlanListResponseDto
     */
    public static TripPlanListResponseDto create(TripPlan tripPlan) {
        TripPlanListResponseDto dto = new TripPlanListResponseDto();
        dto.setTripPlanId(tripPlan.getId());
        dto.setTitle(tripPlan.getTitle());
        dto.setDay(tripPlan.getDay());
        dto.setCreatedDate(DateFormatUtil.DateFormat(tripPlan.getCreatedDate()));
        return dto;
    }

}
