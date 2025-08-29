package auruda.auruda.controller.crawler;

import auruda.auruda.dto.api.ApiResponseDto;
import auruda.auruda.service.crawler.CrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auruda")
public class CrawlerController {

    private final CrawlerService crawlerService;

    /**
     * [컨트롤러]
     * 지역 축제 정보를 크롤링을 통해 가져오는 컨트롤러
     * @param region 지역
     * @param page 페이지 번호
     * @return  List<Map<String, String>>
     */
    @GetMapping("/latest-festivals")
    public ResponseEntity<ApiResponseDto<?>> getLatestFestivals(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "1") int page) {

        return ResponseEntity.ok(ApiResponseDto.success(crawlerService.fetchLatestFestivalData(region, page)));
    }

    /**
     * [컨트롤러]
     * 지역 콘서트 정보를 크롤링을 통해 가져오는 컨트롤러
     * @param page 페이지 번호
     * @param region 지역
     * @param filter 필터
     * @return List<Map<String, String>>
     */
    @GetMapping("/latest-concerts")
    public ResponseEntity<ApiResponseDto<?>> getLatestConcerts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "date") String filter) {

        return ResponseEntity.ok(ApiResponseDto.success(crawlerService.fetchConcertData(page, filter, region)));
    }
}