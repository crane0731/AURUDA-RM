package auruda.auruda.service.comment;

import auruda.auruda.domain.Article;
import auruda.auruda.domain.Comment;
import auruda.auruda.domain.Member;
import auruda.auruda.dto.api.PagedResponseDto;
import auruda.auruda.dto.article.ArticleListResponseDto;
import auruda.auruda.dto.comment.CommentApiResponseDto;
import auruda.auruda.dto.comment.CommentListResponseDto;
import auruda.auruda.dto.comment.CreateCommentRequestDto;
import auruda.auruda.dto.comment.MyCommentListResponseDto;
import auruda.auruda.exception.CommentCustomException;
import auruda.auruda.exception.ErrorMessage;
import auruda.auruda.repository.comment.CommentRepository;
import auruda.auruda.service.article.ArticleService;
import auruda.auruda.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberService memberService;//회원 서비스
    private final ArticleService articleService;//게시글 서비스

    private final CommentRepository commentRepository;//댓글 레파지토리

    /**
     * [서비스 로직]
     * 댓글 등록
     * @param articleId 게시글 아이디
     * @param dto 댓글 생성 요청 DTO
     */
    @Transactional
    public void createComment(Long articleId,CreateCommentRequestDto dto){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //게시글 조회
        Article article = articleService.findById(articleId);

        //댓글 객체 생성
        Comment comment = Comment.create(article, member, dto.getContent());

        //만약 댓글이 대댓글일 경우(부모 댓글이 존재 할 경우)
        settingParent(dto, comment);

        //댓글 저장
        commentRepository.save(comment);
    }

    /**
     * [서비스 로직]
     * 댓글 삭제
     * @param commentId 댓글 아이디
     */
    @Transactional
    public void deleteComment(Long commentId){
        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //댓글 조회
        Comment comment = findById(commentId);

        //현재 회원이 댓글의 주인인지 확인
        validateCommentOwner(comment, member);

        //SOFT DELETE
        comment.softDelete();

    }

    /**
     * [서비스 로직]
     * 해당 게시글의 댓글 목록 조회
     * @param articleId 게시글 아이디
     * @return CommentApiResponseDto
     */
    public CommentApiResponseDto getAllComments(Long articleId){

        //댓글 목록 응답 DTO
        List<CommentListResponseDto> dtoList = getCommentListResponseDtos(articleId);

        //총 댓글 수
        Long count = commentRepository.countByArticleId(articleId);

        //API 응답 DTO 생성 + 반환
        return CommentApiResponseDto.create(count, dtoList);

    }



    /**
     * [서비스 로직]
     * 자신이 쓴 댓글 목록 조회
     * @param page 페이지 번호
     * @return PagedResponseDto<MyCommentListResponseDto>
     */
    public PagedResponseDto<MyCommentListResponseDto> getMyComments(int page){

        //현재 로그인한 회원 조회
        Member member = memberService.findLoginMember();

        //페이징 결과
        Page<Comment> pageResult = commentRepository.findAllByMember(member, PageRequest.of(page, 10));

        //응답 DTO 리스트
        List<MyCommentListResponseDto> content = getMyCommentListResponseDtoList(pageResult);

        //페이징 응답 DTO 생성 + 반환
        return createPagedResponseDto(content,pageResult);

    }

    //== MyCommentListResponseDto 생성==//
    private List<MyCommentListResponseDto> getMyCommentListResponseDtoList(Page<Comment> pageResult) {
        return pageResult.getContent().stream().map(MyCommentListResponseDto::create).toList();
    }


    //==CommentListResponseDto 생성==//
    private List<CommentListResponseDto> getCommentListResponseDtos(Long articleId) {
        return commentRepository.findCommentByArticleId(articleId).stream().map(CommentListResponseDto::create).toList();
    }

    //==페이징 응답 DTO 생성==//
    private PagedResponseDto<MyCommentListResponseDto> createPagedResponseDto(List<MyCommentListResponseDto> content, Page<Comment> pageResult) {
        return PagedResponseDto.<MyCommentListResponseDto>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .build();
    }


    /**
     * [저장]
     * @param comment 댓글
     */
    @Transactional
    public void save(Comment comment) {
        commentRepository.save(comment);
    }


    /**
     * [삭제]
     * @param comment 댓글
     */
    @Transactional
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    /**
     * [조회]
     * PK 값으로 조회
     * @param id PK
     * @return Comment
     */
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(()->new CommentCustomException(ErrorMessage.NOT_FOUND_COMMENT));
    }


    //==부모댓글 - 자식댓글 연관 관계 세팅==//
    private void settingParent(CreateCommentRequestDto dto, Comment comment) {
        if(dto.getParentId()!=null){

            //부모 댓글 조회
            Comment parent = findById(dto.getParentId());

            //부모댓글이 자식댓글일 경우
            if(parent.getParent()!=null){
                throw new CommentCustomException(ErrorMessage.ALREADY_EXISTS_PARENT);
            }

            //자식 댓글 + 부모 댓글 연관관계 세팅
            parent.addChild(comment);
        }
    }


    //==현재 회원이 댓글의 주인인지 확인==//
    private void validateCommentOwner(Comment comment, Member member) {
        if(!comment.getMember().getId().equals(member.getId())){
            throw new CommentCustomException(ErrorMessage.NO_PERMISSION);
        }
    }
}
