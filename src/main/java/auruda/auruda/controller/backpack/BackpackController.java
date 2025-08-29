package auruda.auruda.controller.backpack;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.backpack.BackpackInfoResponseDto;
import auruda.auruda.dto.backpack.CreateBackpackRequestDto;
import auruda.auruda.dto.backpack.SearchBackpackCondDto;
import auruda.auruda.dto.backpack.UpdateBackpackNameRequestDto;
import auruda.auruda.enums.CreatedSortType;
import auruda.auruda.service.backpack.BackpackService;
import auruda.auruda.util.ErrorCheckUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 배낭 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/backpack")
public class BackpackController {

    private final BackpackService backpackService;//배낭 서비스


    /**
     * [컨트롤러]
     * 배낭 생성
     * @param requestDto 요청 DTO
     * @param bindingResult 에러 메시지를 바인딩 할 객체
     * @return 성공 메시지
     */
    @PostMapping("")
    public ResponseEntity<ApiResponseDto<?>> createBackpack(@Valid @RequestBody CreateBackpackRequestDto requestDto, BindingResult bindingResult ) {

        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        backpackService.createBackpack(requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "배낭 등록 성공")));
    }


    /**
     * [컨트롤러]
     * 배낭 삭제
     * @param id 배낭 아이디
     * @return 성공 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteBackpack(@PathVariable("id") Long id){
        backpackService.deleteBackpack(id);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "배낭 삭제 성공")));

    }

    /**
     * [컨트롤러]
     * 배낭 이름 수정
     * @param id 배낭 아이디
     * @param requestDto 요청 DTO
     * @param bindingResult 에러메시지를 바인딩 할 객체
     * @return 성공 메시지
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateBackpackName(@PathVariable("id")Long id,@Valid @RequestBody UpdateBackpackNameRequestDto requestDto, BindingResult bindingResult ) {

        // 오류 메시지를 담을 Map
        Map<String, String> errorMessages = new HashMap<>();

        //필드에러가 있는지 확인
        //오류 메시지가 존재하면 이를 반환
        if (ErrorCheckUtil.errorCheck(bindingResult, errorMessages)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("입력값이 올바르지 않습니다.", errorMessages));
        }

        backpackService.updateBackpackName(id,requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "배낭 이름 수정 성공")));
    }

    /**
     * [컨트롤러]
     * 회원 자신의 배낭 목록 조회
     * @param created 생성일 기준
     * @param page 페이지 번호
     * @return PagedResponseDto<BackpackListResponseDto>
     */
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<?>> getMemberBackpacks(@RequestParam(value = "created",required = false)CreatedSortType created,
                                                                @RequestParam(value = "page", defaultValue = "0")int page
                                                                ){

        return ResponseEntity.ok(ApiResponseDto.success(backpackService.findMemberBackpacks(SearchBackpackCondDto.create(created),page)));

    }


    /**
     * [컨트롤러]
     * 배낭에 장소 추가
     * @param backpackId 배낭 아이디
     * @param placeId 장소 아이디
     * @return 성공 메시지
     */
    @PostMapping("/{backpackId}/place/{placeId}")
    public ResponseEntity<ApiResponseDto<?>> addBackpackPlace(@PathVariable("backpackId")Long backpackId,@PathVariable("placeId")Long placeId) {

        backpackService.addPlace(backpackId,placeId);

        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "배낭에 장소 추가 성공")));

    }

    /**
     * [컨트롤러]
     * 배낭에 장소 삭제
     * @param backpackId 배낭 아이디
     * @param placeId 장소 아이디
     * @return 성공 메시지
     */
    @DeleteMapping("/{backpackId}/place/{placeId}")
    public ResponseEntity<ApiResponseDto<?>> deleteBackpackPlace(@PathVariable("backpackId")Long backpackId,@PathVariable("placeId")Long placeId) {
        backpackService.deletePlace(backpackId,placeId);
        return ResponseEntity.ok(ApiResponseDto.success(Map.of("message", "배낭에 장소 삭제 성공")));

    }

    /**
     * [배낭 상세 조회]
     * @param id 배낭 아이디
     * @return BackpackInfoResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteBackpackPlace(@PathVariable("id")Long id) {

        return ResponseEntity.ok(ApiResponseDto.success(backpackService.findBackpackInfo(id)));

    }


}
