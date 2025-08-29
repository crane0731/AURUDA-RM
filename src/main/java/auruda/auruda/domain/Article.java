package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import auruda.auruda.enums.ArticleType;
import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.ArticleCustomException;
import auruda.auruda.exception.ErrorMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글
 */
@Entity
@Table(name = "article")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="trip_plan_id")
    private TripPlan tripPlan;//여행 계획

    @Column(name = "title", nullable = false)
    private String title; //제목

    @Column(name = "name",nullable = false)
    private String content; //내용

    @Column(name = "like_count", nullable = false)
    private Long likeCount; //추천 수

    @Column(name = "view_count", nullable = false)
    private Long viewCount; //조회 수

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ArticleType type; //게시글 타입

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    @OneToMany(mappedBy = "article",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleImage> articleImages=new ArrayList<>();

    /**
     * [생성 메서드]
     * @param member 회원
     * @param title 제목
     * @param content 내용
     * @param type 게시글 타입
     * @return Article
     */
    public static Article create(Member member,String title, String content, ArticleType type) {
        Article article = new Article();
        article.member = member;
        article.title = title;
        article.content = content;
        article.type = type;
        article.deleteStatus = DeleteStatus.ACTIVE;
        article.tripPlan = null;
        article.likeCount = 0L;
        article.viewCount = 0L;
        return article;
    }

    /**
     * [비즈니스 로직]
     * 업데이트
     * @param title 제목
     * @param content 내용
     * @param type 게시글 타입
     * @param articleImages 게시글 이미지 리스트
     */
    public void update (String title, String content, ArticleType type, List<ArticleImage> articleImages) {
        this.title = title;
        this.content = content;
        this.type = type;
        updateArticleImages(articleImages);
    }

    /**
     * [비즈니스 로직]
     * 게시글 좋아요 +1
     */
    public void addLikeCount(){
        this.likeCount++;
    }

    /**
     * [비즈니스 로직]
     * 게시글 조회수 +1
     */
    public void addViewCount(){
        this.viewCount++;
    }

    /**
     * [비즈니스 로직]
     * 게시글 이미지 업데이트
     * @param articleImages 게시글 이미지 리스트
     */
    private void updateArticleImages(List<ArticleImage> articleImages) {
        this.articleImages.clear();
        this.articleImages.addAll(articleImages);
    }

    /**
     * [연관관계 편의 메서드]
     * Article - ArticleImage
     * @param articleImage 게시글 이미지
     */
    public void addArticleImages(ArticleImage articleImage) {
        articleImages.add(articleImage);
        articleImage.setArticle(this);
    }

    /**
     * [비즈니스 로직]
     * 여행계획 세팅
     * @param tripPlan 여행 계획
     */
    public void settingTripPlan(TripPlan tripPlan) {
            this.tripPlan = tripPlan;
    }

    /**
     * [SOFT DELETE]
     */
    public void softDelete(){

        if(this.deleteStatus.equals(DeleteStatus.DELETED)){
            throw new ArticleCustomException(ErrorMessage.ALREADY_DELETED_ARTICLE);
        }

        //삭제 처리
        this.deleteStatus=DeleteStatus.DELETED;

        this.title="삭제된 게시글";

    }


}
