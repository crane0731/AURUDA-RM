package auruda.auruda.domain;

import auruda.auruda.domain.baseentity.BaseTimeEntity;
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
 * 댓글
 */
@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article; //게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;//부모 댓글

    @Column(name = "content", nullable = false)
    private String content;//내용

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status", nullable = false)
    private DeleteStatus deleteStatus;//삭제 상태

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();


    /**
     * [생성 메서드]
     * @param article 게시글
     * @param member 회원
     * @param content 내용
     * @return Comment
     */
    public static Comment create(Article article, Member member, String content) {
        Comment comment = new Comment();
        comment.article = article;
        comment.member = member;
        comment.content = content;
        comment.deleteStatus = DeleteStatus.ACTIVE;
        comment.parent = null;
        return comment;
    }


    /**
     * [연관 관계 편의 메서드]
     * PARENT - CHILD
     * @param child 자식 댓글
     */
    public void addChild(Comment child) {
        children.add(child);
        child.parent = this;
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

        this.content="삭제된 댓글입니다.";

    }
}
