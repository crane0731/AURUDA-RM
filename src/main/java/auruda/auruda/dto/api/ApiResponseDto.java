package auruda.auruda.dto.api;

import lombok.Getter;
import lombok.Setter;

/**
 * API 응답 형식을 맞추기 위한 DTO
 */
@Getter
@Setter
public class ApiResponseDto<T> {

    private boolean success;     // 성공 여부
    private String message;      // 메시지 (성공 or 오류)
    private T data;              // 실제 응답 데이터 (유형 제네릭)

    public ApiResponseDto(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * api 요청에 성공했을때
     * @param data
     * @return
     * @param <T>
     */
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, "요청이 성공했습니다.", data);
    }

    /**
     * api 요청에 실패했을때
     * @param message
     * @return
     * @param <T>
     */
    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, message, null);
    }

    public static <T> ApiResponseDto<T> error(String message, T data) {
        return new ApiResponseDto<>(false, message, data);
    }



}
