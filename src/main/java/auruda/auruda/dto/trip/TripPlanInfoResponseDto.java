package auruda.auruda.dto.trip;

import auruda.auruda.domain.TripPlan;
import auruda.auruda.domain.TripTheme;
import auruda.auruda.enums.Theme;
import auruda.auruda.util.DateFormatUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 여행 계획 정보 응답 DTO
 */
@Getter
@Setter
public class TripPlanInfoResponseDto {

    private Long tripPlanId; //여행 계획 아이디

    private Long memberId; //회원 아이디
    private String memberEmail; //회원 이메일
    private String nickname;//회원 닉네임

    private String title; // 제목
    private Long travelDay; //여행 일 수
    private String region; //지역
    private String startDate; //여행 시작일
    private String endDate; //여행 종료일
    private List<Theme> themes; //테마


    private List<TripPlaceListResponseDto> places;//장소들

    private String createdDate; //생성일
    private String updatedDate; //수정일


    /**
     * [생성 메서드]
     * @param tripPlan 여행 계획
     * @return TripPlanInfoResponseDto
     */
    public static TripPlanInfoResponseDto create(TripPlan tripPlan) {
        TripPlanInfoResponseDto dto = new TripPlanInfoResponseDto();
        dto.setTripPlanId(tripPlan.getId());
        dto.setMemberId(tripPlan.getMember().getId());
        dto.setMemberEmail(tripPlan.getMember().getEmail());
        dto.setNickname(tripPlan.getMember().getNickname());

        dto.setTitle(tripPlan.getTitle());
        dto.setRegion(tripPlan.getRegion());
        dto.setStartDate(tripPlan.getStartDate());
        dto.setEndDate(tripPlan.getEndDate());

        dto.setThemes(tripPlan.getTripThemes().stream().map(TripTheme::getTheme).toList());

        dto.setPlaces(tripPlan.getTripPlaces().stream().map(TripPlaceListResponseDto::create).toList());
        dto.setCreatedDate(DateFormatUtil.DateFormat(tripPlan.getCreatedDate()));
        dto.setUpdatedDate(DateFormatUtil.DateFormat(tripPlan.getUpdatedDate()));
        return dto;
    }
}
