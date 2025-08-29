package auruda.auruda.controller.place;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.dto.place.SearchPlaceCondDto;
import auruda.auruda.enums.Category;
import auruda.auruda.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 장소 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/place")
public class PlaceController {

    private final PlaceService placeService;//장소 서비스

    /**
     * [컨트롤러]
     * 장소 상세 조회
     * @param id 장소 아이디
     * @return PlaceInfoResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getPlaceInfo(@PathVariable("id")Long id){
        return ResponseEntity.ok(ApiResponseDto.success(placeService.getPlaceInfo(id)));

    }

    /**
     * [컨트롤러]
     * 방문자수가 높은 순대로 장소 목록 조회
     * @param city 지역
     * @param category 카테고리
     * @param page 페이지 번호
     * @return PagedResponseDto<PlaceListResponseDto>
     */
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<?>> getAllPlaces(@RequestParam(value = "city", required = false)String city,
                                                          @RequestParam(value = "category",required = false) Category category,
                                                          @RequestParam(value = "page",defaultValue = "0") int page
                                                          ){

        return ResponseEntity.ok(ApiResponseDto.success(placeService.findAllByCond(SearchPlaceCondDto.create(city,category),page)));
    }


}
