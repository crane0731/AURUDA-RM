package auruda.auruda.service.place;

import auruda.auruda.domain.Place;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.place.PlaceInfoResponseDto;
import auruda.auruda.dto.place.PlaceListResponseDto;
import auruda.auruda.dto.place.SearchPlaceCondDto;
import auruda.auruda.dto.review.ReviewStatsDto;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.PlaceCusomException;
import auruda.auruda.repository.place.PlaceRepository;
import auruda.auruda.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 장소 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final ReviewService reviewService;//리뷰 서비스
    private final PlaceRepository placeRepository;//장소 레파지토리

    /**
     * [서비스 로직]
     * 장소 정보 상세 조회
     * @param placeId 장소 아이디
     * @return PlaceInfoResponseDto
     */
    public PlaceInfoResponseDto getPlaceInfo(Long placeId){
        //장소 조회
        Place place = findById(placeId);

        //응답 DTO 생성 및 반환
        return PlaceInfoResponseDto.create(place);
    }


    /**
     * [서비스 로직]
     * 방문자수가 높은 순서대로 장소 목록 조회
     * @param cond 검색 조건 DTO
     * @param page 페이지 번호
     * @return PagedResponseDto<PlaceListResponseDto>
     */
    public PagedResponseDto<PlaceListResponseDto>findAllByCond(SearchPlaceCondDto cond, int page){

        //페이징된 장소 목록 조회
        Page<Place> pagedPlace = placeRepository.findAllByCond(cond, PageRequest.of(page, 10));

        //집계 세팅 + 응답 DTO 반환
        List<PlaceListResponseDto> content = createPlaceListResponseDto(pagedPlace);

        //페이징된 응답 DTO 생성 + 반환
        return createPagedResponseDto(content,pagedPlace);

    }




    /**
     * [조회]
     * PK 값으로 조회
     * @param placeId PK
     * @return Place
     */
    public Place findById(Long placeId){
        return placeRepository.findById(placeId).orElseThrow(()->new PlaceCusomException(ErrorMessage.NOT_FOUND_PLACE));
    }


    /**
     * [조회]
     * PK 값으로 조회
     * 리뷰 fetch join
     * @param placeId PK
     * @return Place
     */
    public Place findWithReviewsByID(Long placeId){
        return placeRepository.findWithReviewsById(placeId).orElseThrow(()->new PlaceCusomException(ErrorMessage.NOT_FOUND_PLACE));
    }

    /**
     * [조회]
     * 이름 + 주소로 조회
     * @param name 장소 이름
     * @param address 주소
     * @return Place
     */
    public Place findPlaceByNameAndAddress(String name, String address){
        return placeRepository.findByNameAndAddress(name, address).orElseThrow(()->new PlaceCusomException(ErrorMessage.NOT_FOUND_PLACE));
    }

    //== 응답 DTO 생성==//
    private List<PlaceListResponseDto> createPlaceListResponseDto(Page<Place> pagedPlace) {
        List<Long> placeIds = pagedPlace.getContent().stream()
                .map(Place::getId)
                .toList();

        Map<Long, ReviewStatsDto> statsMap = reviewService.findReviewStatsByPlaceIds(placeIds);

        return pagedPlace.getContent().stream()
                .map(place -> {
                    PlaceListResponseDto dto = PlaceListResponseDto.create(place);
                    ReviewStatsDto stats = statsMap.get(place.getId());
                    dto.setRatingAvg(stats != null ? stats.getAvg() : 0);
                    dto.setReviewedCount(stats != null ? stats.getCount() : 0);
                    return dto;
                })
                .toList();
    }

    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<PlaceListResponseDto> createPagedResponseDto(List<PlaceListResponseDto> content, Page<Place> page) {
        return PagedResponseDto.<PlaceListResponseDto>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }



}
