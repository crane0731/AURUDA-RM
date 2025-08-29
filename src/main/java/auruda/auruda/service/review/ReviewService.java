package auruda.auruda.service.review;

import auruda.auruda.domain.Member;
import auruda.auruda.domain.Place;
import auruda.auruda.domain.Review;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.member.MemberListResponseDto;
import auruda.auruda.dto.review.*;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.PlaceCusomException;
import auruda.auruda.exception.ReviewCustomException;
import auruda.auruda.repository.place.PlaceRepository;
import auruda.auruda.repository.review.ReviewRepository;
import auruda.auruda.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 리뷰 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {


    private final MemberService memberService;//회원 서비스


    private final PlaceRepository placeRepository;//장소 레파지토리
    private final ReviewRepository reviewRepository;//리뷰 레파지토리


    /**
     * [서비스 로직]
     * 리뷰 등록
     * @param placeId 장소 아이디
     * @param dto 리뷰 등록 요청 DTO
     */
    @Transactional
    public void createReview(Long placeId,CreateReviewRequestDto dto) {

        //리뷰 점수 검증
        validateRating(dto);

        //현재 로그인한 회원
        Member member = memberService.findLoginMember();

        //장소 조회
        Place place = getPlace(placeId);

        //이미 작성한 리뷰가 있는지 확인
        validateExistsReview(member, place);

        //리뷰 생성 + 저장
        save(Review.create(member,place, dto.getContent(), dto.getRating()));
    }

    /**
     * [서비스 로직]
     * SOFT DELETE
     * @param reviewId 리뷰 아이디
     */
    @Transactional
    public void softDelete(Long reviewId) {

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //리뷰 조회
        Review review = findById(reviewId);

        //삭제하려는 리뷰가 회원의 것인지 확인
        validateReviewOwner(member, review);

        //SOFT DELETE
        review.softDelete();
    }


    /**
     * [서비스 로직]
     * 리뷰 수정
     * @param reviewId 리뷰 아이디
     * @param dto 리뷰 수정 요청 DTO
     */
    @Transactional
    public void updateReview(Long reviewId,UpdateReviewRequestDto dto){
        //로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //리뷰 조회
        Review review = findById(reviewId);

        //수정하려는 리뷰가 회원의 것인지 확인
        validateReviewOwner(member, review);

        //리뷰 업데이트
        review.update(dto.getContent(), dto.getRating());

    }

    /**
     * [서비스 로직]
     * 리뷰 목록 조회
     * @param placeId 장소 아이디
     * @param cond 검색 조건 DTO
     * @param page 페이지 번호
     * @return PagedResponseDto<ReviewListResponseDto>
     */
    public PagedResponseDto<ReviewListResponseDto> findAllByPlaceAndCond(Long placeId,SearchReviewCondDto cond, int page){

        //장소 조회
        Place place = getPlace(placeId);

        //페이징 된 리뷰 목록 조회
        Page<Review> pagedReviewList = reviewRepository.findAllByPlaceAndCond(place, cond, createPageable(page));

        //리뷰 목록 내용 조회
        List<ReviewListResponseDto> content = pagedReviewList.getContent().stream().map(ReviewListResponseDto::create).toList();

        //페이징 응답 DTO 생성 + 반환
        return createPagedResponseDto(content,pagedReviewList);

    }


    /**
     * [서비스 로직]
     * 전달받은 placeIds에 해당하는 장소들의 리뷰 통계(평균 평점, 리뷰 개수)를
     * 한 번에 조회하여 placeId를 key로 하는 Map으로 반환한다.
     * @param placeIds 리뷰 통계를 조회할 장소 ID 목록
     * @return  Map<Long, ReviewStatsDto>
     */
    public Map<Long, ReviewStatsDto> findReviewStatsByPlaceIds(List<Long> placeIds) {
        return reviewRepository.findStatsByPlaceIds(placeIds)
                .stream()
                .collect(Collectors.toMap(
                        ReviewStatsDto::getPlaceId,
                        dto -> dto
                ));
    }

    /**
     * [저장]
     * @param review 리뷰
     */
    @Transactional
    public void save(Review review) {
        reviewRepository.save(review);
    }

    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return Review
     */
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow(()->new ReviewCustomException(ErrorMessage.NOT_FOUND_REVIEW));
    }



    //==리뷰 점수 검증==//
    private void validateRating(CreateReviewRequestDto dto) {
        if(dto.getRating()<0){
            throw new ReviewCustomException(ErrorMessage.REVIEW_RATING_NOT_UNDER_ZERO);
        }
    }

    //==장소 조회==//
    private Place getPlace(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow(() -> new PlaceCusomException(ErrorMessage.NOT_FOUND_PLACE));
    }

    //==이미 작성한 리뷰가 있는지 확인==//
    private void validateExistsReview(Member member, Place place) {
        Review review = reviewRepository.findByMemberAndPlace(member, place).orElse(null);

        if (review != null) {
            if(review.getDeleteStatus().equals(DeleteStatus.ACTIVE)) {
                throw new ReviewCustomException(ErrorMessage.ALREADY_EXISTS_REVIEW);
            }
        }
    }

    //==삭제하려는 리뷰가 회원의 것인지 확인==//
    private void validateReviewOwner(Member member, Review review) {
        if(!member.getId().equals(review.getMember().getId())) {
            throw new ReviewCustomException(ErrorMessage.NO_PERMISSION);
        }
    }


    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<ReviewListResponseDto> createPagedResponseDto(List<ReviewListResponseDto> content, Page<Review> pagedReviewList) {
        return PagedResponseDto.<ReviewListResponseDto>builder()
                .content(content)
                .page(pagedReviewList.getNumber())
                .size(pagedReviewList.getSize())
                .totalPages(pagedReviewList.getTotalPages())
                .totalElements(pagedReviewList.getTotalElements())
                .first(pagedReviewList.isFirst())
                .last(pagedReviewList.isLast())
                .build();
    }

    //==페이징 객체 생성==//
    private Pageable createPageable(int page) {
        return PageRequest.of(page,10);
    }
}
