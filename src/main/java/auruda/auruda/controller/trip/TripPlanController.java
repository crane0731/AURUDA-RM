package auruda.auruda.controller.trip;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.trip.CreateTripPlanRequestDto;
import auruda.auruda.dto.trip.TripPlanInfoResponseDto;
import auruda.auruda.dto.trip.UpdateTripPlanRequestDto;
import auruda.auruda.service.tripplan.TripPlanService;
import auruda.auruda.util.ErrorCheckUtil;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 여행 계획 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/trip-plan")
public class TripPlanController {

    private final TripPlanService tripPlanService;//여행계획 서비스

    /**
     * [컨트롤러]
     * 여행 계획 저장
     * @param requestDto 요청 DTO
     * @param bindingResult 에러메시지를 바인딩 할 객체
     * @return 성공 메시지
     */
    @PostMapping("")
    public ResponseEntity<ApiResponseDto<?>> createTripPlan(@Valid @RequestBody CreateTripPlanRequestDto requestDto, BindingResult bindingResult) {
        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        tripPlanService.createTripPlan(requestDto);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "여행계획 저장 성공")));


    }

    /**
     * [컨트롤러]
     * 여행 계획 삭제
     * @param id 여행계획 아이디
     * @return 성공 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteTripPlan(@PathVariable("id")Long id) {
        tripPlanService.deleteTripPlan(id);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "여행계획 삭제 성공")));

    }

    /**
     * [컨트롤러]
     * 여행 계획 수정
     * @param id 여행 계획 아이디
     * @param requestDto 수정 요청 DTO
     * @param bindingResult 에러 메시지를 바인딩할 객체
     * @return 성공 메시지
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateTripPlan(@PathVariable("id")Long id,@Valid @RequestBody UpdateTripPlanRequestDto requestDto, BindingResult bindingResult) {
        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        tripPlanService.updateTripPlan(id,requestDto);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "여행계획 수정 성공")));


    }

    /**
     * [컨트롤러]
     * 여행 계획 상세 조회
     * @param id 여행계획 아이디
     * @return TripPlanInfoResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getTripPlanInfo(@PathVariable("id")Long id) {
        return ResponseEntity.ok(ApiResponseDto.success(tripPlanService.getTripPlanInfo(id)));

    }


    /**
     * [컨트롤러]
     * 나의 여행 계획 목록 조회
     * @param page 페이지 번호
     * @return PagedResponseDto<TripPlanListResponseDto>
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<?>> getMyTripPlans(@RequestParam(value = "page",defaultValue = "0")int page) {
        return ResponseEntity.ok(ApiResponseDto.success(tripPlanService.getMyTripPlans(page)));

    }


}
