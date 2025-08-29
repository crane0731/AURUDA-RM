package auruda.auruda.service.backpack;

import auruda.auruda.domain.Backpack;
import auruda.auruda.domain.BackpackPlace;
import auruda.auruda.domain.Member;
import auruda.auruda.domain.Place;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.backpack.*;
import auruda.auruda.dto.place.PlaceListResponseDto;
import auruda.auruda.dto.review.ReviewStatsDto;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.BackpackCustomException;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.repository.backpack.BackpackRepository;
import auruda.auruda.repository.backpackplace.BackpackPlaceRepository;
import auruda.auruda.repository.review.ReviewRepository;
import auruda.auruda.service.member.MemberService;
import auruda.auruda.service.place.PlaceService;
import auruda.auruda.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 배낭 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackpackService {

    private final MemberService memberService;//회원 서비스
    private final PlaceService placeService;//장소 서비스
    private final ReviewService reviewService;//리뷰 서비스

    private final BackpackRepository backpackRepository;//배낭 레파지토리
    private final BackpackPlaceRepository backpackPlaceRepository;//배낭-장소 레파지토리


    /**
     * [서비스 로직]
     * 배낭 생성
     * @param dto 요청 DTO
     */
    @Transactional
    public void createBackpack(CreateBackpackRequestDto dto) {

        //로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //배낭 엔티티 객체 생성 + 저장
        backpackRepository.save(Backpack.create(member, dto.getName()));
    }

    /**
     * [서비스 로직]
     * 배낭 논리 삭제 처리 (SOFT DELETE)
     * @param backpackId 배낭 아이디
     */
    @Transactional
    public void deleteBackpack(Long backpackId) {

        //로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //배낭 조회
        Backpack backpack = findById(backpackId);

        //회원이 배낭의 주인인지 확인
        validateBackpackOwner(member, backpack);

        //SOFT DELETE
        backpack.softDelete();

    }


    /**
     * [서비스 로직]
     * 배낭 이름 수정
     * @param backpackId 배낭 아이디
     * @param dto 수정 요청 DTO
     */
    @Transactional
    public void updateBackpackName(Long backpackId, UpdateBackpackNameRequestDto dto){

        //로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //배낭 조회
        Backpack backpack = findById(backpackId);

        //회원이 배낭의 주인인지 확인
        validateBackpackOwner(member, backpack);

        //배낭 이름 수정
        backpack.updateName(dto.getName());

    }

    /**
     * [서비스 로직]
     * 배낭 목록 조회
     * @param cond 검색 조건 DTO
     * @param page 페이지 번호
     * @return PagedResponseDto<BackpackListResponseDto>
     */
    public PagedResponseDto<BackpackListResponseDto> findMemberBackpacks(SearchBackpackCondDto cond, int page){

        //로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //페이지 객체 조회
        Page<Backpack> pagedBackpackList = backpackRepository.findAllByMemberAndCond(member, cond, createPageable(page));

        //페이지 내용 조회
        List<BackpackListResponseDto> content = pagedBackpackList.getContent().stream().map(BackpackListResponseDto::create).toList();

        //페이징된 배낭 목록 응답 DTO 생성 + 반환
        return createPagedResponseDto(content,pagedBackpackList);

    }

    /**
     * [서비스 로직]
     * 배낭에 장소 추가
     * @param backpackId 배낭 아이디
     * @param placeId 장소 아이디
     */
    @Transactional
    public void addPlace(Long backpackId,Long placeId){
        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //배낭 조회
        Backpack backpack = findById(backpackId);

        //배낭이 현재 로그인한 회원의 것인지 확인
        validateBackpackOwner(backpack, member);

        //장소 조회
        Place place = placeService.findById(placeId);

        //이미 배낭에 해당 장소가 존재하는지 확인
        if(validateExistsBackpackPlace(backpack, place)){
           return;
        }

        //배낭에 장소 추가
        backpack.addPlace(place);
    }



    /**
     * [서비스 로직] 베낭 장소 삭제(SOFT DELETE)
     * @param backpackId 배낭 아이디
     * @param placeId 장소 아이디
     */
    @Transactional
    public void deletePlace(Long backpackId,Long placeId){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //배낭 조회
        Backpack backpack = findById(backpackId);

        //배낭이 현재 로그인한 회원의 것인지 확인
        validateBackpackOwner(backpack, member);

        //장소 조회
        Place place = placeService.findById(placeId);

        //배낭 장소 조회
        BackpackPlace backpackPlace = getBackpackPlace(backpack, place);

        //SOFT DELETE
        backpackPlace.softDelete();

    }

    /**
     * [서비스 로직]
     * 배낭 상세 조회
     * @param backpackId 배낭 아이디
     * @return  BackpackInfoResponseDto
     */
    @Transactional
    public BackpackInfoResponseDto findBackpackInfo(Long backpackId){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //배낭 조회
        Backpack backpack = findById(backpackId);

        //배낭이 현재 로그인한 회원의 것인지 확인
        validateBackpackOwner(backpack, member);

        //장소 목록 응답 DTO 생성
        List<PlaceListResponseDto> placeListResponseDtoList = getPlaceListResponseDtos(backpack);

        //최종 응답 DTO 생성 + 반환
        return BackpackInfoResponseDto.create(backpack, placeListResponseDtoList);
    }



    /**
     * [저장]
     * @param backpack 배낭
     */
    @Transactional
    public void save(Backpack backpack) {
        backpackRepository.save(backpack);
    }

    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return  Backpack
     */
    public Backpack findById(Long id){
        return backpackRepository.findById(id).orElseThrow(()-> new BackpackCustomException(ErrorMessage.NOT_FOUND_BACKPACK));
    }

    //==회원이 배낭의 주인인지 확인 하는 메서드==//
    private void validateBackpackOwner(Member member, Backpack backpack) {
        if(!member.getId().equals(backpack.getMember().getId())) {
            throw new BackpackCustomException(ErrorMessage.NO_PERMISSION);
        }
    }

    //==페이지 요청 객체 생성==//
    private PageRequest createPageable(int page) {
        return PageRequest.of(page, 10);
    }

    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<BackpackListResponseDto> createPagedResponseDto(List<BackpackListResponseDto> content, Page<Backpack> pagedBackpackList) {
        return PagedResponseDto.<BackpackListResponseDto>builder()
                .content(content)
                .page(pagedBackpackList.getNumber())
                .size(pagedBackpackList.getSize())
                .totalPages(pagedBackpackList.getTotalPages())
                .totalElements(pagedBackpackList.getTotalElements())
                .first(pagedBackpackList.isFirst())
                .last(pagedBackpackList.isLast())
                .build();
    }

    //==배낭이 현재 로그인한 회원의 것인지 확인하는 메서드==//
    private void validateBackpackOwner(Backpack backpack, Member member) {
        if(!backpack.getMember().getId().equals(member.getId())){
            throw new BackpackCustomException(ErrorMessage.NO_PERMISSION);
        }
    }

    //==이미 배낭 - 장소가 존재하는지 확인==//
    private boolean validateExistsBackpackPlace(Backpack backpack, Place place) {
        BackpackPlace backpackPlace = backpackPlaceRepository.findByBackpackAndPlace(backpack, place).orElse(null);

        if(backpackPlace!=null){
            if(backpackPlace.getDeleteStatus().equals(DeleteStatus.ACTIVE)){
                throw new BackpackCustomException(ErrorMessage.ALREADY_EXISTS_BACKPACK_PLACE);
            }
            else if(backpackPlace.getDeleteStatus().equals(DeleteStatus.DELETED)){
                backpackPlace.recovery();
                return true;
            }
        }
        return false;
    }

    //==배낭 장소 조회==//
    private BackpackPlace getBackpackPlace(Backpack backpack, Place place) {
        return backpackPlaceRepository.findByBackpackAndPlace(backpack, place).orElseThrow(() -> new BackpackCustomException(ErrorMessage.NOT_FOUND_BACKPACK_PLACE));
    }


    //==장소 목록 응답 DTO 생성 메서드==//
    private List<PlaceListResponseDto> getPlaceListResponseDtos(Backpack backpack) {

        //장소 목록 조회
        List<Place> places = getPlaces(backpack);

        //장소 아이디 목록 조회
        List<Long> placeIds = getPlaceIds(places);

        //집계 쿼리 결과 Map
        Map<Long, ReviewStatsDto> statsMap = reviewService.findReviewStatsByPlaceIds(placeIds);


        // DTO 생성 + 매핑
        return places.stream()
                .map(place -> {
                    PlaceListResponseDto dto = PlaceListResponseDto.create(place);
                    ReviewStatsDto stats = statsMap.get(place.getId());
                    dto.setRatingAvg(stats != null ? stats.getAvg() : 0);
                    dto.setReviewedCount(stats != null ? stats.getCount() : 0);
                    return dto;
                })
                .toList();

    }

    //==장소 목록 조회==//
    private List<Place> getPlaces(Backpack backpack) {
        return backpackPlaceRepository.findByBackpack(backpack)
                .stream()
                .map(BackpackPlace::getPlace)
                .toList();
    }

    //==장소 아이디 목록 조회==//
    private List<Long> getPlaceIds(List<Place> places) {
        return places.stream()
                .map(Place::getId)
                .toList();
    }


}
