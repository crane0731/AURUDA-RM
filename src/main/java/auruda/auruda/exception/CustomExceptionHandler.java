package auruda.auruda.exception;

import auruda.auruda.dto.api.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 커스텀 예외 핸들러
 */
@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MemberCustomException.class)
    public ResponseEntity<Object> handleMemberCustomException(MemberCustomException ex) {
        log.error("CustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(JwtCustomException.class)
    public ResponseEntity<Object> handleJwtCustomException(JwtCustomException ex) {
        log.error("JwtCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(ImageCustomException.class)
    public ResponseEntity<Object> handleImageCustomException(ImageCustomException ex) {
        log.error("ImageCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(ArticleCustomException.class)
    public ResponseEntity<Object> handleArticleCustomException(ArticleCustomException ex) {
        log.error("ArticleCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(CommentCustomException.class)
    public ResponseEntity<Object> handleCommentCustomException(CommentCustomException ex) {
        log.error("CommentCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(PlaceCusomException.class)
    public ResponseEntity<Object> handlePlaceCusomException(PlaceCusomException ex) {
        log.error("PlaceCusomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(ReviewCustomException.class)
    public ResponseEntity<Object> handleReviewCustomException(ReviewCustomException ex) {
        log.error("ReviewCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(BackpackCustomException.class)
    public ResponseEntity<Object> handleBackpackCustomException(BackpackCustomException ex) {
        log.error("BackpackCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(TripPlanCustomException.class)
    public ResponseEntity<Object> handleTripPlanCustomException(TripPlanCustomException ex) {
        log.error("TripPlanCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(TripPlaceCustomException.class)
    public ResponseEntity<Object> handleTripPlaceCustomException(TripPlaceCustomException ex) {
        log.error("TripPlaceCustomException: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ex.getMessage()));
    }
}
