package auruda.auruda.controller.openai;

import auruda.auruda.dto.api.ApiResponseDto;

import auruda.auruda.dto.trip.CreateItineraryRequestDto;
import auruda.auruda.dto.trip.TouristSpotDto;
import auruda.auruda.dto.trip.TouristSpotRequestDto;
import auruda.auruda.service.gpt.OpenAiService;
import auruda.auruda.service.publicdata.PublicDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 여행 여정 컨트롤러
 * PUBLIC DATA + OPEN AI
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auruda/itinerary")
public class itineraryController {

    private final OpenAiService openAiService; //open ai 서비스
    private final PublicDataService publicDataService;//공공데이터 서비스

    /**
     * [컨트롤러]
     * 추천 여행지를 보여주는 컨트롤러
     * @param requestDto 요청 DTO
     * @return List<TouristSpotDto>
     */
    @PostMapping("places")
    public ResponseEntity<ApiResponseDto<?>> getPlaceData(@RequestBody TouristSpotRequestDto requestDto){
        return ResponseEntity.ok(ApiResponseDto.success(publicDataService.getTouristSpots(requestDto.getCity(),requestDto.getContentTypeId(), requestDto.getAreaCode(), requestDto.getSigunguCode(),requestDto.getPage())));
    }



    /**
     * [컨트롤러]
     * 여행 코스를 추천해주는 컨트롤러
     * @param requestDto 요청 DTO
     * @return List<GptPlaceResponseDto>
     * @throws Exception
     */
    @PostMapping("")
    public ResponseEntity<ApiResponseDto<?>> getItinerary(@RequestBody CreateItineraryRequestDto requestDto) throws Exception {

        List<TouristSpotDto> places = getTouristSpotDtos(requestDto);

        return ResponseEntity.ok().body(ApiResponseDto.success(openAiService.getTravelItinerary(requestDto, places)));

    }


    //==여행지 장소 데이터 목록을 생성하는 메서드==//
    private List<TouristSpotDto> getTouristSpotDtos(CreateItineraryRequestDto requestDto) {
        List<TouristSpotDto> places=new ArrayList<>();
        List<TouristSpotDto> touristSpots1 = publicDataService.getTouristSpots(requestDto.getCity(), 38, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots1);

        List<TouristSpotDto> touristSpots2 = publicDataService.getTouristSpots(requestDto.getCity(),39, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots2);

        List<TouristSpotDto> touristSpots3 = publicDataService.getTouristSpots(requestDto.getCity(),32, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots3);

        List<TouristSpotDto> touristSpots4 = publicDataService.getTouristSpots(requestDto.getCity(),15, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots4);

        List<TouristSpotDto> touristSpots5 = publicDataService.getTouristSpots(requestDto.getCity(),14, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots5);

        List<TouristSpotDto> touristSpots6 = publicDataService.getTouristSpots(requestDto.getCity(),12, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots6);

        List<TouristSpotDto> touristSpots7 = publicDataService.getTouristSpots(requestDto.getCity(),28, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots7);

        List<TouristSpotDto> touristSpots8 = publicDataService.getTouristSpots(requestDto.getCity(),1, requestDto.getAreaCode(), requestDto.getSigunguCode(),1);
        places.addAll(touristSpots8);
        return places;
    }


}
