package auruda.auruda.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 페이징을 응답을 위한 DTO
 * @param <T>
 */
@Getter
@Builder
@AllArgsConstructor
public class PagedResponseDto<T> {

    private List<T> content;     // 실제 데이터
    private int page;            // 현재 페이지 번호 (0부터 시작)
    private int size;            // 페이지당 개수
    private int totalPages;      // 전체 페이지 수
    private long totalElements;  // 전체 데이터 수
    private boolean first;       // 첫 페이지 여부
    private boolean last;        // 마지막 페이지 여부

}
