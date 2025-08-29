package auruda.auruda.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 상태 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class ReviewStatsDto {
    private Long placeId;
    private Double avg;
    private Long count;
}
