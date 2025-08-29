package auruda.auruda.domain;

import auruda.auruda.enums.DeleteStatus;
import auruda.auruda.exception.ArticleCustomException;
import auruda.auruda.exception.ErrorMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 좋아요
 */
@Entity
@Table(name = "like_artice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_artice_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article; //게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //회원

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태


    /**
     * [생성 메서드]
     * @param article 게시글
     * @param member 회원
     * @return LikeArticle
     */
    public static LikeArticle create(Article article, Member member) {
        LikeArticle likeArticle = new LikeArticle();
        likeArticle.article = article;
        likeArticle.member = member;
        likeArticle.deleteStatus = DeleteStatus.ACTIVE;
        return likeArticle;
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

    }


}
