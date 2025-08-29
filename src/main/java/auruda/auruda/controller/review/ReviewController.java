package auruda.auruda.controller.review;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.review.CreateReviewRequestDto;
import auruda.auruda.dto.review.SearchReviewCondDto;
import auruda.auruda.dto.review.UpdateReviewRequestDto;
import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.enums.ReviewSortType;
import auruda.auruda.service.review.ReviewService;
import auruda.auruda.util.ErrorCheckUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 리뷰 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/review")
public class ReviewController {

    private final ReviewService reviewService;//리뷰 서비스

    /**
     * [컨트롤러]
     * 리뷰 등록
     * @param id 장소 아이디
     * @param requestDto 리뷰 등록 요청 DTO
     * @param bindingResult 에러 메시지를 바인딩할 객체
     * @return 성공 메시지
     */
    @PostMapping("/place/{id}")
    public ResponseEntity<ApiResponseDto<?>> createReview(@PathVariable("id")Long id , @Valid @RequestBody CreateReviewRequestDto requestDto, BindingResult bindingResult) {

        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        reviewService.createReview(id, requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "리뷰 등록 성공")));

    }


    /**
     * [컨트롤러]
     * 리뷰 삭제
     * @param id 리뷰 아이디
     * @return 성공 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteReview(@PathVariable("id")Long id){
        reviewService.softDelete(id);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "리뷰 삭제 성공")));

    }

    /**
     * [컨트롤러]
     * 리뷰 수정
     * @param id 리뷰 아이디
     * @param requestDto  요청 DTO
     * @param bindingResult 에러메시지를 바인딩할 객체
     * @return 성공 메시지
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateReview(@PathVariable("id")Long id , @Valid @RequestBody UpdateReviewRequestDto requestDto, BindingResult bindingResult){

        reviewService.updateReview(id, requestDto);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "리뷰 수정 성공")));

    }

    /**
     * [컨트롤러]
     * 특정 장소의 리뷰 목록 조회
     * @param id 장소 아이디
     * @param sortType 정렬 조건
     * @param created 생성일 기준
     * @param page 페이지 번호
     * @return PagedResponseDto<ReviewListResponseDto>
     */
    @GetMapping("/place/{id}")
    public ResponseEntity<ApiResponseDto<?>> getAllReviewsByPlace(@PathVariable("id")Long id,
                                                                  @RequestParam(value = "sortType",required = false)ReviewSortType sortType,
                                                                  @RequestParam(value = "created",required = false)CreatedSortType created,
                                                                  @RequestParam(value = "page", defaultValue = "0")int page
                                                                  ){


        return ResponseEntity.ok(ApiResponseDto.success(reviewService.findAllByPlaceAndCond(id, SearchReviewCondDto.create(sortType, created), page)));

    }

}
