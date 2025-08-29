package auruda.auruda.controller.kakaomap;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.service.kakaomap.KakaoMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auruda/kakao/places")
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;//카카오 장소 서비스

    /**
     * [컨트롤러]
     * 카카오 맵 컨트롤러
     * (위도,경도)주변 장소의 카테고리 조회
     * @param latitude 위도
     * @param longitude 경도
     * @param category 카테고리
     * @return  String
     */
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<?>> getPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String category) {
        return ResponseEntity.ok(ApiResponseDto.success(kakaoMapService.getPlaces(latitude, longitude, category)));
    }
}