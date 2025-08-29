package auruda.auruda.service.article;

import auruda.auruda.domain.*;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.article.*;
import auruda.auruda.dto.comment.CommentCountDto;
import auruda.auruda.enums.ArticleType;
import auruda.auruda.exception.ArticleCustomException;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.exception.ImageCustomException;
import auruda.auruda.exception.TripPlanCustomException;
import auruda.auruda.repository.comment.CommentRepository;
import auruda.auruda.repository.article.ArticleRepository;
import auruda.auruda.service.image.UploadedArticleImageService;
import auruda.auruda.service.member.MemberService;
import auruda.auruda.service.tripplan.TripPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 게시글 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final MemberService memberService;//회원 서비스
    private final UploadedArticleImageService articleImageService;//게시글 이미지 서비스
    private final TripPlanService tripPlanService;//여행 계획 서비스

    private final ArticleRepository articleRepository; //게시글 레파지토리
    private final CommentRepository commentRepository;//댓글 레파지토리

    /**
     * [서비스 로직]
     * 게시글 등록
     * @param dto 요청 DTO
     */
    @Transactional
    public void createArticle(CreateArticleRequestDto dto){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //게시글 엔티티 객체 생성
        Article article = Article.create(member, dto.getTitle(), dto.getContent(), dto.getArticleType());

        //게시글 업로드 이미지 사용 처리 + 게시글 이미지 생성
        List<ArticleImage> articleImages = createArticleImage(dto.getImageUrls(), article);

        //게시글에 게시글 이미지 세팅
        articleImages.forEach(article::addArticleImages);

        //게시글 타입이 여행 후기, 또는 여행 평가 라면 여행 계획 세팅
        if(dto.getArticleType().equals(ArticleType.TRIP_REVIEW) || dto.getArticleType().equals(ArticleType.TRIP_FEEDBACK)){

            //TripPlanId가 null 이라면 에러를 던짐
            if(dto.getTripPlanId()==null){
                throw new TripPlanCustomException(ErrorMessage.NOT_FOUND_TRIP_PLAN);
            }

            //연관 관계 세팅
            TripPlan tripPlan = tripPlanService.findById(dto.getTripPlanId());
            article.settingTripPlan(tripPlan);
        }

        //게시글 저장
        articleRepository.save(article);
    }

    /**
     * [서비스 로직]
     * 게시글 수정
     * @param articleId 게시글 아이디
     * @param dto 수정 요청 DTO
     */
    @Transactional
    public void updateArticle(Long articleId, UpdateArticleRequestDto dto) {

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //게시글 조회
        Article article = findById(articleId);

        //회원이 게시글의 작성자인지 확인
        validateArticleOwner(article, member);

        //기존의 업로드 이미지들 비사용 처리
        unusedOldImage(article);

        //게시글 업로드 이미지 사용 처리 + 게시글 이미지 생성
        List<ArticleImage> articleImages = createArticleImage(dto.getImageUrls(), article);

        //게시글 업데이트
        article.update(dto.getTitle(), dto.getContent(), dto.getArticleType(), articleImages);
        
    }

    /**
     * [서비스 로직]
     * 게시글 삭제 (SOFT DELETE)
     * @param articleId 게시글 아이디
     */
    @Transactional
    public void deleteArticle(Long articleId){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //게시글 조회
        Article article = findById(articleId);

        //게시글이 로그인한 회원의 것인지 확인
        validateArticleOwner(article, member);

        //SOFT DELETE
        article.softDelete();

    }



    /**
     * [서비스 로직]
     * 게시글 상세 조회
     * @param articleId 게시글 아이디
     * @return ArticleInfoResponseDto
     */
    @Transactional
    public ArticleInfoResponseDto getArticleInfo(Long articleId){
        Article article = findOne(articleId);
        //조회수 증가
        article.addViewCount();
        return ArticleInfoResponseDto.create(article);
    }


    /**
     * [서비스 로직]
     * 게시글 목록 조회
     * @param cond 검색 조건
     * @param page 페이지 번호
     * @return PagedResponseDto<ArticleListResponseDto>
     */
    public PagedResponseDto<ArticleListResponseDto> getAllArticles(SearchArticleListCondDto cond,int page){

        Page<Article> pagedArticleList = articleRepository.findAllByCond(cond, PageRequest.of(page, 10));

        //응답 DTO 생성 + 집계 세팅
        List<ArticleListResponseDto> content = getArticleListResponseDtos(pagedArticleList);


        return createPagedResponseDto(content, pagedArticleList);
    }



    /**
     * [서비스 로직]
     * 자신의 게시글 목록 조회
     * @param cond 검색 조건
     * @param page 페이지 번호
     * @return PagedResponseDto<ArticleListResponseDto>
     */
    public PagedResponseDto<ArticleListResponseDto> getMyArticles(SearchMyArticleCondDto cond,int page){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        Page<Article> pagedArticleList = articleRepository.findAllByCondAndMember(member, cond, PageRequest.of(page, 10));

        //응답 DTO 생성 + 집계 세팅
        List<ArticleListResponseDto> content = getArticleListResponseDtos(pagedArticleList);


        return createPagedResponseDto(content, pagedArticleList);

    }


    /**
     * [저장]
     * @param article 게시글
     */
    @Transactional
    public void save(Article article) {
        articleRepository.save(article);
    }

    /**
     * [삭제]
     * @param article 게시글
     */
    @Transactional
    public void delete(Article article) {
        articleRepository.delete(article);
    }

    /**
     * [조회]
     * PK 값으로 게시글 조회 + FETCH JOIN MEMBER
     * @param articleId 게시글 아이디
     * @return Article
     */
    public Article findOne(Long articleId) {
        return articleRepository.findOne(articleId).orElseThrow(() -> new ArticleCustomException(ErrorMessage.NOT_FOUND_ARTICLE));
    }

    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return Article
     */
    public Article findById(Long id) {
        return articleRepository.findById(id).orElseThrow(()-> new ArticleCustomException(ErrorMessage.NOT_FOUND_ARTICLE));
    }


    //==게시글 업로드 이미지 사용 처리 + 게시글 이미지 생성==//
    private List<ArticleImage> createArticleImage(List<String> imageUrls, Article article) {

        List<ArticleImage> articleImages = new ArrayList<>();

        for (String imageUrl : imageUrls) {

            UploadedImage uploadedImage = articleImageService.findByImageUrl(imageUrl);
            if(uploadedImage == null){
                throw new ImageCustomException(ErrorMessage.NOT_FOUND_FILE);
            }
            else {
                uploadedImage.usedImage();
                ArticleImage articleImage = ArticleImage.create(article, imageUrl);
                articleImages.add(articleImage);
            }
        }
        return articleImages;
    }


    //==게시글이 로그인한 회원의 것인지 확인==//
    private void validateArticleOwner(Article article, Member member) {
        if(!article.getMember().getId().equals(member.getId())){
            throw new ArticleCustomException(ErrorMessage.NO_PERMISSION);
        }
    }

    //==기존의 업로드 이미지들 비사용 처리==//
    private void unusedOldImage(Article article) {
        article.getArticleImages().forEach(imageUrl -> {
            UploadedImage uploadedImage = articleImageService.findByImageUrl(imageUrl.getImageUrl());
            uploadedImage.unusedImage();
        });
    }

    //==응답 DTO 생성 + 집계 세팅//==
    private List<ArticleListResponseDto> getArticleListResponseDtos(Page<Article> pagedArticleList) {
        List<Long> articleIds = pagedArticleList.getContent().stream().map(Article::getId).toList();

        //댓글 수 집계 한 번에 조회 → Map<articleId, count>
        Map<Long, Long> commentCountMap = commentRepository.findCommentCount(articleIds).stream()
                .collect(Collectors.toMap(
                        CommentCountDto::getArticleId,
                        CommentCountDto::getCount
                ));

        //DTO 생성 + 집계 주입 (N+1 없음)
        List<ArticleListResponseDto> content = pagedArticleList.getContent().stream()
                .map(a -> {
                    ArticleListResponseDto dto = ArticleListResponseDto.create(a);
                    dto.setCommentCount(commentCountMap.getOrDefault(a.getId(), 0L));
                    return dto;
                })
                .toList();
        return content;
    }


    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<ArticleListResponseDto> createPagedResponseDto(List<ArticleListResponseDto> content, Page<Article> pagedArticleList) {
        return PagedResponseDto.<ArticleListResponseDto>builder()
                .content(content)
                .page(pagedArticleList.getNumber())
                .size(pagedArticleList.getSize())
                .totalPages(pagedArticleList.getTotalPages())
                .totalElements(pagedArticleList.getTotalElements())
                .first(pagedArticleList.isFirst())
                .last(pagedArticleList.isLast())
                .build();
    }

}
