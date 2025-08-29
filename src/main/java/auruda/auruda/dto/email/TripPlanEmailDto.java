package auruda.auruda.dto.email;

import lombok.*;

/**
 * 여행 계획 이메일 DTO
 */

@Data
@AllArgsConstructor
public class TripPlanEmailDto {

    private String title;
    private String email;
    private String region;
    private String startDate;  // 여행 시작 날짜
    private String endDate;    // 여행 끝 날짜
}
