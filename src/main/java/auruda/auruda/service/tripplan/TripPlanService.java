package auruda.auruda.service.tripplan;

import auruda.auruda.domain.*;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.article.ArticleListResponseDto;
import auruda.auruda.dto.place.CreatePlaceRequestDto;
import auruda.auruda.dto.trip.CreateTripPlanRequestDto;
import auruda.auruda.dto.trip.TripPlanInfoResponseDto;
import auruda.auruda.dto.trip.TripPlanListResponseDto;
import auruda.auruda.dto.trip.UpdateTripPlanRequestDto;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.TripPlanCustomException;
import auruda.auruda.repository.place.PlaceRepository;
import auruda.auruda.repository.tripplan.TripPlanRepository;
import auruda.auruda.service.member.MemberService;
import auruda.auruda.service.place.PlaceService;
import auruda.auruda.service.tripplace.TripPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 여행 계획 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripPlanService {

    private final MemberService memberService;//회원 서비스
    private final TripPlaceService tripPlaceService;//여행 장소 서비스
    private final PlaceService placeService;//장소 서비스


    private final TripPlanRepository tripPlanRepository;//여행계획 레파지토리
    private final PlaceRepository placeRepository;//장소 레파지토리



    /**
     * [서비스 로직]
     * 여행 계획 저장
     * @param dto
     */
    @Transactional
    public void createTripPlan(CreateTripPlanRequestDto dto){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //여행 계획 엔티티 객체 생성
        TripPlan tripPlan = TripPlan.create(member, dto.getTitle(), dto.getRegion(), dto.getTravelDay(), dto.getStartDate(), dto.getEndDate(), dto.getTheme1(), dto.getTheme2(), dto.getTheme3());

        //저장
        save(tripPlan);

        //장소 엔티티 객체 생성 + 저장
        Map<Long, List<Place>> places = createPlaces(dto);

        //여행 장소 생성
        createTripPlaces(places, tripPlan);


    }


    /**
     * [서비스 로직]
     * 여행 계획 삭제
     * @param id 여행 계획 아이디
     */
    @Transactional
    public void deleteTripPlan(Long id){
        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //여행 계획 조회
        TripPlan tripPlan = findById(id);

        //현재 로그인한 회원이 여행 계획의 주인인지 확인
        validateTripPlanOwner(tripPlan, member);

        //SOFT DELETE
        tripPlan.softDelete();

    }


    /**
     * [여행 계획 수정]
     * @param tripPlanId 여행 계획 아이디
     * @param dto 수정 요청 DTO
     */
    @Transactional
    public void updateTripPlan(Long tripPlanId, UpdateTripPlanRequestDto dto){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //여행 계획 조회
        TripPlan tripPlan = findById(tripPlanId);

        //현재 로그인한 회원이 여행 계획의 주인인지 확인
        validateTripPlanOwner(tripPlan, member);

        //여행 계획 업데이트
        tripPlan.update(dto.getTitle(), dto.getRegion(), dto.getTravelDay(), dto.getStartDate(), dto.getEndDate(), dto.getTheme1(), dto.getTheme2(), dto.getTheme3());

        //여행계획 아이디로 여행장소 리스트 삭제
        tripPlaceService.deleteByTripPlan(tripPlan);

        //장소 엔티티 객체 생성 + 저장
        Map<Long, List<Place>> places = createPlacesByUpdate(dto);

        //여행 장소 생성
        createTripPlaces(places, tripPlan);

    }


    /**
     * [서비스 로직]
     * 여행 계획 상세 조회
     * @param tripPlanId 여행 계획 아이디
     * @return TripPlanInfoResponseDto
     */
    public TripPlanInfoResponseDto getTripPlanInfo(Long tripPlanId) {

        //여행 계획 조회
        TripPlan tripPlan = findOne(tripPlanId);

        //응답 DTO 생성 + 응답
        return  TripPlanInfoResponseDto.create(tripPlan);
    }

    /**
     * [서비스 로직]
     * 나의 여행 계획 목록 조회
     * @param page 페이지  번호
     * @return PagedResponseDto<TripPlanListResponseDto>
     */
    public PagedResponseDto<TripPlanListResponseDto> getMyTripPlans(int page){
        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //페이지 결과
        Page<TripPlan> pageResult = tripPlanRepository.findAllByMember(member, PageRequest.of(page, 10));

        //응답 DTO
        List<TripPlanListResponseDto> content = pageResult.getContent().stream().map(TripPlanListResponseDto::create).toList();

        return createPagedResponseDto(content,pageResult);
    }




    /**
     * [저장]
     * @param tripPlan 여행 계획
     */
    @Transactional
    public void save(TripPlan tripPlan) {
        tripPlanRepository.save(tripPlan);
    }

    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return  TripPlan
     */
    public TripPlan findById(Long id) {
        return tripPlanRepository.findById(id).orElseThrow(()->new TripPlanCustomException(ErrorMessage.NOT_FOUND_TRIP_PLAN));
    }

    /**
     * [조회]
     * PK 값으로 조회 + FETCH JOIN TripPlace and Place
     * @param id PK 값
     * @return TripPlan
     */
    public  TripPlan findOne(Long id){
        return tripPlanRepository.findOne(id).orElseThrow(()->new TripPlanCustomException(ErrorMessage.NOT_FOUND_TRIP_PLAN));
    }


    //==현재 로그인한 회원이 여행 계획의 주인인지 확인==//
    private void validateTripPlanOwner(TripPlan tripPlan, Member member) {
        if(!tripPlan.getMember().getId().equals(member.getId())){
            throw new TripPlanCustomException(ErrorMessage.NO_PERMISSION);
        }
    }

    //==여행 장소 생성==//
    private void createTripPlaces(Map<Long, List<Place>> places, TripPlan tripPlan) {
        for (Long day : places.keySet()) {
            List<Place> dayPlaces = places.get(day);
            for (Place dayPlace : dayPlaces) {
                TripPlace tripPlace = TripPlace.create(tripPlan, dayPlace,day);
                //저장
                tripPlaceService.save(tripPlace);
            }

        }
    }

    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<TripPlanListResponseDto> createPagedResponseDto(List<TripPlanListResponseDto> content, Page<TripPlan> pageResult) {
        return PagedResponseDto.<TripPlanListResponseDto>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .build();
    }


    //==장소 엔티티 객체 생성
    //먼저 이름과 주소로 장소엔티티를 찾은 다음에 삭제상태가 아닌 장소가 있다면 새로 저장하지말고 방문자수만 증가시킴
    //그렇지 않으면 새로운 장소 엔티티 생성 및 저장==//
    private Map<Long, List<Place>> createPlaces(CreateTripPlanRequestDto dto) {
        return dto.getPlaces().stream()
                .collect(Collectors.groupingBy(
                        CreatePlaceRequestDto::getDay,
                        Collectors.mapping(placeDto -> {
                            Place place = placeRepository.findByNameAndAddress(placeDto.getName(), placeDto.getAddress())
                                    .orElse(null);

                            if (place != null) {
                                if (place.getDeleteStatus().equals(DeleteStatus.ACTIVE)) {
                                    place.plusVisitedCount();
                                } else {
                                    place.recovery();
                                }
                                return place;
                            } else {
                                Place newPlace = Place.create(
                                        placeDto.getName(),
                                        placeDto.getDescription(),
                                        placeDto.getCity(),
                                        placeDto.getAddress(),
                                        placeDto.getLat(),
                                        placeDto.getLng(),
                                        placeDto.getCategory()
                                );
                                return placeRepository.save(newPlace);
                            }
                        }, Collectors.toList())
                ));
    }

    //==장소 엔티티 객체 생성
    //먼저 이름과 주소로 장소엔티티를 찾은 다음에 삭제상태가 아닌 장소가 있다면 새로 저장하지말고 방문자수만 증가시킴
    //그렇지 않으면 새로운 장소 엔티티 생성 및 저장==//
    private Map<Long, List<Place>> createPlacesByUpdate(UpdateTripPlanRequestDto dto) {
        return dto.getPlaces().stream()
                .collect(Collectors.groupingBy(
                        CreatePlaceRequestDto::getDay,
                        Collectors.mapping(placeDto -> {
                            Place place = placeRepository.findByNameAndAddress(placeDto.getName(), placeDto.getAddress())
                                    .orElse(null);

                            if (place != null) {
                                if (place.getDeleteStatus().equals(DeleteStatus.ACTIVE)) {
                                    place.plusVisitedCount();
                                } else {
                                    place.recovery();
                                }
                                return place;
                            } else {
                                Place newPlace = Place.create(
                                        placeDto.getName(),
                                        placeDto.getDescription(),
                                        placeDto.getCity(),
                                        placeDto.getAddress(),
                                        placeDto.getLat(),
                                        placeDto.getLng(),
                                        placeDto.getCategory()
                                );
                                return placeRepository.save(newPlace);
                            }
                        }, Collectors.toList())
                ));
    }

}
