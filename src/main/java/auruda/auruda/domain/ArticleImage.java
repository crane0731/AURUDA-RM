package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 이미지
 */
@Entity
@Table(name = "article_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_image_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;//게시글

    @Column(name = "image_url", nullable = false)
    private String imageUrl;// 이미지 URL

    /**
     * [생성 메서드]
     * @param article 게시글
     * @param imageUrl 이미지 URL
     * @return ArticleImage
     */
    public static ArticleImage create(Article article, String imageUrl) {
        ArticleImage articleImage = new ArticleImage();
        articleImage.article = article;
        articleImage.imageUrl = imageUrl;
        return articleImage;
    }


    /**
     * [SETTER]
     * @param article 게시글
     */
    protected void setArticle(Article article) {
        this.article = article;
    }

}
