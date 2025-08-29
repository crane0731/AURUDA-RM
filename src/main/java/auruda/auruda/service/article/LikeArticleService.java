package auruda.auruda.service.article;

import auruda.auruda.domain.Article;
import auruda.auruda.domain.LikeArticle;
import auruda.auruda.domain.Member;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.article.ArticleListResponseDto;
import auruda.auruda.dto.comment.CommentCountDto;
import auruda.auruda.exception.ArticleCustomException;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.repository.comment.CommentRepository;
import auruda.auruda.repository.like.LikeArticleRepository;
import auruda.auruda.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 게시글 좋아요 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeArticleService {

    private final MemberService memberService; //회원 서비스
    private final ArticleService articleService;//게시글 서비스
    private final LikeArticleRepository likeArticleRepository;//게시글 좋아요 레파지토리
    private final CommentRepository commentRepository;//게시글 레파지토리


    /**
     * [서비스 로직]
     * 게시글 좋아요
     * @param articleId 게시글 아이디
     */
    @Transactional
    public void like(Long articleId){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //게시글 조회
        Article article = articleService.findById(articleId);

        //엔티티 객체 생성
        LikeArticle.create(article,member);

        //저장
        likeArticleRepository.save(LikeArticle.create(article,member));

        //게시글 좋아요 수 +1
        article.addLikeCount();

        //게시글을 작성한 회원
        Member writer = article.getMember();

        //포인트 증가
        writer.plusPoint();

    }

    /**
     * [서비스 로직]
     * 회원이 좋아요한 게시글 리스트 목록 조회
     * @param page 페이지 번호
     * @return PagedResponseDto
     */
    public PagedResponseDto<ArticleListResponseDto> getLikeArticles(int page){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //좋아요 게시글 조회
        Page<LikeArticle> pageResult = likeArticleRepository.findAllByMember(member, PageRequest.of(page, 10));

        //게시글 리스트 조회
        List<Article> articleList = pageResult.getContent().stream().map(LikeArticle::getArticle).toList();

        //응답 DTO 생성 + 집계 세팅
        List<ArticleListResponseDto> content = getArticleListResponseDtos(articleList);

        return  createPagedResponseDto(content,pageResult);

    }



    /**
     * [저장]
     * @param likeArticle 게시글 좋아요
     */
    @Transactional
    public void save(LikeArticle likeArticle) {
        likeArticleRepository.save(likeArticle);
    }


    /**
     * [삭제]
     * @param likeArticle 게시글 좋아요
     */
    @Transactional
    public void delete(LikeArticle likeArticle) {
        likeArticleRepository.delete(likeArticle);
    }


    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return LikeArticle
     */
    @Transactional
    public LikeArticle findById(Long id){
        return likeArticleRepository.findById(id).orElseThrow(()->new ArticleCustomException(ErrorMessage.NOT_FOUND_LIKE_ARTICLE));
    }


    //==응답 DTO 생성 + 집계 세팅//==
    private List<ArticleListResponseDto> getArticleListResponseDtos(List<Article> articleList) {

        List<Long> articleIds = articleList.stream().map(Article::getId).toList();
        //댓글 수 집계 한 번에 조회 → Map<articleId, count>
        Map<Long, Long> commentCountMap = commentRepository.findCommentCount(articleIds).stream()
                .collect(Collectors.toMap(
                        CommentCountDto::getArticleId,
                        CommentCountDto::getCount
                ));

        //DTO 생성 + 집계 주입 (N+1 없음)
        List<ArticleListResponseDto> content = articleList.stream()
                .map(a -> {
                    ArticleListResponseDto dto = ArticleListResponseDto.create(a);
                    dto.setCommentCount(commentCountMap.getOrDefault(a.getId(), 0L));
                    return dto;
                })
                .toList();

        return content;
    }

    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<ArticleListResponseDto> createPagedResponseDto(List<ArticleListResponseDto> content, Page<LikeArticle> pageResult) {
        return PagedResponseDto.<ArticleListResponseDto>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .build();
    }
}
